//! Derives the SPL Stake Pool IDL, `idls/spl_stake_pool.json`, from the published
//! `spl-stake-pool` crate, so that the file is never edited by hand.
//!
//! The program publishes no IDL and carries no Shank or Codama annotations, so nothing
//! upstream can be asked for one. What it does publish is the crate, at the release that is
//! deployed, and every instruction has a builder function in it. This tool links that crate
//! and takes each part of the IDL from the strongest source the crate offers:
//!
//! * **Account lists** are what upstream's builders return. Each builder is called with
//!   sentinel keys, and the returned `AccountMeta`s give the order and the writable and
//!   signer flags. An account only some builders of an instruction send is optional, and one
//!   they disagree about signing is `"either"`. Nothing here reads the enum's doc comments
//!   for a flag: they disagree with the builders in places. Where they do, the builder is
//!   never the weaker of the two, so what it sends is accepted; in one place it is the
//!   stronger, asking `Initialize`'s custom deposit authority for a signature the program
//!   does not check, and the IDL follows the builder there too.
//! * **Account names** are the builders' parameter names, read from each builder's
//!   signature in the crate's source and matched to the keys in the order they were passed.
//!   No name is typed here, except for the three addresses a builder derives instead of
//!   taking as a parameter, and for the well-known programs and sysvars.
//! * **Argument names** of a struct variant are its field names, used here to construct the
//!   real enum value, so a renamed field stops this tool compiling. A tuple variant has no
//!   field names; each of its fields takes the name of the one builder parameter of its
//!   type, which every builder of the instruction must have under the same name.
//! * **Argument types and order** are checked against bytes. A value of the wrong kind
//!   cannot be converted into the field, the values encoded here in the order they are
//!   listed must be the bytes of the enum serialized directly, and those must be the data
//!   of every builder's instruction.
//! * **Defined types** come from the crate's own Borsh schema.
//! * **Errors** are enumerated from the crate, code by code.
//! * **Docs and error messages** are upstream's doc comments, read from the crate's source
//!   as text, with a variant's `#[deprecated]` note in front. They describe; they decide
//!   nothing, and where upstream's prose is wrong this carries it. The `[w]`/`[s]` markers
//!   are dropped, and an instruction whose documented account list is not the length of
//!   the built one gets no account docs at all.
//!
//! The source read is the crate that was linked: the lock file is compiled in, and the tool
//! refuses to run once the lock file on disk differs from it.
//!
//! Usage, from the repository root:
//!
//! ```shell
//! cargo run --locked --manifest-path tools/stake-pool-idl/Cargo.toml -- idls/spl_stake_pool.json
//! cargo run --locked --manifest-path tools/stake-pool-idl/Cargo.toml -- --check idls/spl_stake_pool.json
//! ```
//!
//! The first writes the file; `--check` derives it and fails if the file on disk differs.

// Deprecated builders are still the only description of instructions the program dispatches.
#![allow(deprecated)]

use {
    borsh::{
        schema::{BorshSchemaContainer, Definition, Fields},
        BorshSchema,
    },
    num_traits::FromPrimitive,
    serde_json::{json, Map, Value},
    solana_instruction::Instruction,
    solana_pubkey::Pubkey,
    spl_stake_pool::{
        error::StakePoolError,
        find_deposit_authority_program_address, find_withdraw_authority_program_address,
        inline_mpl_token_metadata::pda::find_metadata_account,
        instruction::{self as builders, FundingType, PreferredValidatorType, StakePoolInstruction},
        state::{Fee, FeeType, ValidatorList, ValidatorStakeInfo},
    },
    std::{
        cell::RefCell,
        collections::HashMap,
        fs,
        num::NonZeroU32,
        path::{Path, PathBuf},
        process::{exit, Command},
    },
};

/// Addresses a builder fills in by itself. An account at one of these is given the address as
/// its default value, and the name here.
const CONSTANTS: &[(&str, &str)] = &[
    ("11111111111111111111111111111111", "systemProgram"),
    ("SysvarC1ock11111111111111111111111111111111", "clockSysvar"),
    ("SysvarRent111111111111111111111111111111111", "rentSysvar"),
    ("SysvarStakeHistory1111111111111111111111111", "stakeHistorySysvar"),
    ("Stake11111111111111111111111111111111111111", "stakeProgram"),
    ("StakeConfig11111111111111111111111111111111", "stakeConfig"),
    ("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s", "mplTokenMetadata"),
];

thread_local! {
    /// Addresses a builder derives by itself, under the name this tool gives them.
    static DERIVED: RefCell<HashMap<Pubkey, &'static str>> = RefCell::new(HashMap::new());
    /// The keys passed to the builder being called, in the order of its parameters.
    static PASSED: RefCell<Vec<Option<Pubkey>>> = const { RefCell::new(Vec::new()) };
    /// Every builder's parameters, by function name: (name, type), in order.
    static SIGNATURES: RefCell<HashMap<String, Vec<(String, String)>>> =
        RefCell::new(HashMap::new());
    static NEXT: RefCell<u16> = const { RefCell::new(0) };
}

/// A fresh sentinel key. It has no name until it is passed to a builder.
fn key() -> Pubkey {
    let n = NEXT.with(|next| {
        let mut next = next.borrow_mut();
        *next += 1;
        *next
    });
    let mut bytes = [0u8; 32];
    bytes[0] = 0xEE;
    bytes[1] = (n >> 8) as u8;
    bytes[2] = n as u8;
    Pubkey::new_from_array(bytes)
}

/// Passes `key` as the builder's next key parameter. Arguments are evaluated left to right,
/// so the order of these calls is the order of the parameters.
fn pass(key: &Pubkey) -> &Pubkey {
    PASSED.with(|passed| passed.borrow_mut().push(Some(*key)));
    key
}

/// Passes a fresh sentinel as the builder's next key parameter.
fn arg() -> &'static Pubkey {
    pass(Box::leak(Box::new(key())))
}

/// Passes an optional key parameter, present or absent.
fn optional(key: Option<&Pubkey>) -> Option<&Pubkey> {
    PASSED.with(|passed| passed.borrow_mut().push(key.copied()));
    key
}

/// Names an address a builder derives by itself instead of taking as a parameter.
fn derived(name: &'static str, address: Pubkey) {
    DERIVED.with(|derived| derived.borrow_mut().insert(address, name));
}

/// The parameters of every `pub fn` in instruction.rs.
fn read_signatures(instruction_rs: &str) {
    let mut signatures = HashMap::new();
    for (start, _) in instruction_rs.match_indices("\npub fn ") {
        let declaration = &instruction_rs[start + "\npub fn ".len()..];
        let (name, rest) = declaration.split_once('(').unwrap();
        let parameters = rest.split_once(')').unwrap().0;
        let parameters: Vec<(String, String)> = parameters
            .split(',')
            .filter_map(|parameter| parameter.split_once(':'))
            .map(|(name, kind)| (name.trim().to_string(), kind.trim().to_string()))
            .collect();
        signatures.insert(name.trim().to_string(), parameters);
    }
    SIGNATURES.with(|all| *all.borrow_mut() = signatures);
}

fn signature(function: &str) -> Vec<(String, String)> {
    SIGNATURES.with(|all| {
        all.borrow().get(function).cloned().unwrap_or_else(|| panic!("no `pub fn {function}`"))
    })
}

/// A builder's key parameters: (name, whether it may be absent). `&Pubkey` is a key the
/// builder must be given, `Option<Pubkey>` and `Option<&Pubkey>` one it may be given.
/// `program_id` is not an account of the instruction and is left out.
fn key_parameters(function: &str) -> Vec<(String, bool)> {
    signature(function)
        .into_iter()
        .filter(|(name, _)| name != "program_id")
        .filter_map(|(name, kind)| match kind.as_str() {
            "&Pubkey" => Some((name, false)),
            "Option<Pubkey>" | "Option<&Pubkey>" => Some((name, true)),
            _ => None,
        })
        .collect()
}

/// A value for one instruction argument. Its kind is the argument's IDL type.
#[derive(Clone, Debug)]
enum Val {
    U8(u8),
    U32(u32),
    U64(u64),
    Bool(bool),
    Str(&'static str),
    OptionKey(Option<Pubkey>),
    Fee(Fee),
    FeeType(FeeType),
    FundingType(FundingType),
    PreferredValidatorType(PreferredValidatorType),
}

/// Converting a value into the real field is what ties its kind to the field's Rust type:
/// each conversion accepts its own kind and nothing else.
macro_rules! into_field {
    ($($kind:ident => $real:ty),* $(,)?) => {$(
        impl From<Val> for $real {
            fn from(value: Val) -> Self {
                match value {
                    Val::$kind(inner) => inner.into(),
                    other => panic!("{other:?} is not a {}", stringify!($real)),
                }
            }
        }
    )*};
}
into_field! {
    U8 => u8, U32 => u32, U64 => u64, Bool => bool, Str => String, OptionKey => Option<Pubkey>,
    Fee => Fee, FeeType => FeeType, FundingType => FundingType,
    PreferredValidatorType => PreferredValidatorType,
}

fn number(format: &str) -> Value {
    json!({"kind": "numberTypeNode", "format": format, "endian": "le"})
}

fn link(type_name: &str) -> Value {
    json!({"kind": "definedTypeLinkNode", "name": lower_camel(type_name)})
}

impl Val {
    /// The value's bytes as the type node below describes them, written out here and not
    /// by Borsh, so that comparing them with the enum's serialization checks the node.
    fn encode(&self, out: &mut Vec<u8>) {
        match self {
            Val::U8(value) => out.push(*value),
            Val::U32(value) => out.extend(value.to_le_bytes()),
            Val::U64(value) => out.extend(value.to_le_bytes()),
            Val::Bool(value) => out.push(*value as u8),
            Val::Str(value) => {
                out.extend((value.len() as u32).to_le_bytes());
                out.extend(value.as_bytes());
            }
            Val::OptionKey(None) => out.push(0),
            Val::OptionKey(Some(key)) => {
                out.push(1);
                out.extend(key.to_bytes());
            }
            // A defined type's own layout is the schema's to describe.
            Val::Fee(value) => out.extend(borsh::to_vec(value).unwrap()),
            Val::FeeType(value) => out.extend(borsh::to_vec(value).unwrap()),
            Val::FundingType(value) => out.extend(borsh::to_vec(value).unwrap()),
            Val::PreferredValidatorType(value) => out.extend(borsh::to_vec(value).unwrap()),
        }
    }

    /// The builder parameter types that feed a tuple field of this kind.
    fn parameter_types(&self) -> &'static [&'static str] {
        match self {
            Val::U32(_) => &["u32", "Option<NonZeroU32>"],
            Val::U64(_) => &["u64"],
            Val::FundingType(_) => &["FundingType"],
            other => panic!("no rule names a tuple field holding {other:?}"),
        }
    }

    fn type_node(&self) -> Value {
        match self {
            Val::U8(_) => number("u8"),
            Val::U32(_) => number("u32"),
            Val::U64(_) => number("u64"),
            Val::Bool(_) => json!({"kind": "booleanTypeNode", "size": number("u8")}),
            Val::Str(_) => json!({
                "kind": "sizePrefixTypeNode",
                "type": {"kind": "stringTypeNode", "encoding": "utf8"},
                "prefix": number("u32"),
            }),
            Val::OptionKey(_) => json!({
                "kind": "optionTypeNode",
                "fixed": false,
                "item": {"kind": "publicKeyTypeNode"},
                "prefix": number("u8"),
            }),
            Val::Fee(_) => link("Fee"),
            Val::FeeType(_) => link("FeeType"),
            Val::FundingType(_) => link("FundingType"),
            Val::PreferredValidatorType(_) => link("PreferredValidatorType"),
        }
    }
}

/// One enum variant: its name, its arguments in order, and the variant serialized directly.
struct Encoded {
    variant: &'static str,
    /// The arguments in order. A tuple variant's are unnamed until its builders are known.
    args: Vec<(Option<&'static str>, Val)>,
    data: Vec<u8>,
}

/// A struct variant. The field names are the argument names, and the real variant is built
/// from them, so both are checked by the compiler against the crate.
macro_rules! fields {
    ($variant:ident { $($field:ident: $value:expr),* $(,)? }) => {
        Encoded {
            variant: stringify!($variant),
            args: vec![$((Some(stringify!($field)), $value)),*],
            data: borsh::to_vec(&StakePoolInstruction::$variant { $($field: $value.into()),* })
                .unwrap(),
        }
    };
}

/// A tuple variant. Its fields have no names in the crate; `tuple_names` finds each one's
/// in the builders' signatures.
macro_rules! tuple {
    ($variant:ident($($value:expr),* $(,)?)) => {
        Encoded {
            variant: stringify!($variant),
            args: vec![$((None, $value)),*],
            data: borsh::to_vec(&StakePoolInstruction::$variant($($value.into()),*)).unwrap(),
        }
    };
}

macro_rules! unit {
    ($variant:ident) => {
        Encoded {
            variant: stringify!($variant),
            args: vec![],
            data: borsh::to_vec(&StakePoolInstruction::$variant).unwrap(),
        }
    };
}

/// What one upstream builder returned for this program, and the name of each key it was
/// given: the name of the parameter the key was passed as.
struct Built {
    function: &'static str,
    instruction: Instruction,
    names: HashMap<Pubkey, String>,
}

struct Spec {
    encoded: Encoded,
    built: Vec<Built>,
}

/// Records what `function` returned. The keys passed since the last call are matched, in
/// order, to the key parameters of its signature.
fn one(function: &'static str, instruction: Instruction) -> Built {
    let passed: Vec<Option<Pubkey>> =
        PASSED.with(|passed| std::mem::take(&mut *passed.borrow_mut()));
    let parameters = key_parameters(function);
    assert_eq!(
        passed.len(),
        parameters.len(),
        "{function} takes the keys {parameters:?} and was passed {} here",
        passed.len()
    );
    let mut names = HashMap::new();
    for ((parameter, may_be_absent), key) in parameters.into_iter().zip(passed) {
        match key {
            Some(key) => assert!(names.insert(key, parameter).is_none(), "{function}: a key twice"),
            None => assert!(may_be_absent, "{function}: {parameter} is not optional"),
        }
    }
    Built { function, instruction, names }
}

/// The stake deposit builders return the stake authorizations too; the pool's own
/// instruction is the one addressed to the program.
fn own(program: &Pubkey, function: &'static str, instructions: Vec<Instruction>) -> Built {
    let mut own: Vec<Instruction> =
        instructions.into_iter().filter(|instruction| instruction.program_id == *program).collect();
    assert_eq!(own.len(), 1, "{function} returned {} stake pool instructions", own.len());
    one(function, own.remove(0))
}

fn fee(denominator: u64, numerator: u64) -> Fee {
    Fee { denominator, numerator }
}

const A: u64 = 0x1122_3344_5566_7788;
const B: u64 = 0x0102_0304_0506_0708;
const C: u64 = 0x1112_1314_1516_1718;
const D: u64 = 0x2122_2324_2526_2728;

/// Every instruction, in the order of `StakePoolInstruction`, with every public builder shape
/// that changes its account list. Each key parameter is given a sentinel with `arg()`, or
/// `pass()` where the key is needed again. Arguments are the same in each call of one
/// instruction, so the builders' data can be compared with the variant serialized directly.
fn specs(p: &Pubkey) -> Vec<Spec> {
    let (f1, f2, f3) = (fee(A, B), fee(C, D), fee(D, A));
    let initialize = |deposit_authority: Option<&Pubkey>| {
        builders::initialize(
            p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
            optional(deposit_authority).copied(), f1, f2, f3, 0x7F, 0x0A0B_0C0D,
        )
    };
    let set_funding_authority = |authority: Option<&Pubkey>| {
        let funding_type = FundingType::SolWithdraw;
        builders::set_funding_authority(p, arg(), arg(), optional(authority), funding_type)
    };
    let vote = key();
    let mut validators = ValidatorList::new(4);
    validators.validators = vec![ValidatorStakeInfo::default(); 3];
    vec![
        Spec {
            encoded: fields!(Initialize {
                fee: Val::Fee(f1),
                withdrawal_fee: Val::Fee(f2),
                deposit_fee: Val::Fee(f3),
                referral_fee: Val::U8(0x7F),
                max_validators: Val::U32(0x0A0B_0C0D),
            }),
            built: vec![
                one("initialize", initialize(None)),
                one("initialize", initialize(Some(&key()))),
            ],
        },
        Spec {
            encoded: tuple!(AddValidatorToPool(Val::U32(0x0102_0304))),
            built: vec![one(
                "add_validator_to_pool",
                builders::add_validator_to_pool(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    NonZeroU32::new(0x0102_0304),
                ),
            )],
        },
        Spec {
            encoded: unit!(RemoveValidatorFromPool),
            built: vec![one(
                "remove_validator_from_pool",
                builders::remove_validator_from_pool(p, arg(), arg(), arg(), arg(), arg(), arg()),
            )],
        },
        Spec {
            encoded: fields!(DecreaseValidatorStake {
                lamports: Val::U64(A),
                transient_stake_seed: Val::U64(B),
            }),
            built: vec![one(
                "decrease_validator_stake",
                builders::decrease_validator_stake(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), A, B,
                ),
            )],
        },
        Spec {
            encoded: fields!(IncreaseValidatorStake {
                lamports: Val::U64(A),
                transient_stake_seed: Val::U64(B),
            }),
            built: vec![one(
                "increase_validator_stake",
                builders::increase_validator_stake(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B,
                ),
            )],
        },
        Spec {
            encoded: fields!(SetPreferredValidator {
                validator_type: Val::PreferredValidatorType(PreferredValidatorType::Withdraw),
                validator_vote_address: Val::OptionKey(Some(vote)),
            }),
            built: vec![one(
                "set_preferred_validator",
                builders::set_preferred_validator(
                    p, arg(), arg(), arg(), PreferredValidatorType::Withdraw,
                    optional(Some(&vote)).copied(),
                ),
            )],
        },
        // The validator and transient stake accounts this instruction updates follow the
        // fixed seven, a pair for every validator in the range. An IDL has no way to say
        // that, so both builders are asked for an empty range and the fixed accounts are
        // what is described.
        Spec {
            encoded: fields!(UpdateValidatorListBalance {
                start_index: Val::U32(2),
                no_merge: Val::Bool(true),
            }),
            built: vec![
                one(
                    "update_validator_list_balance",
                    builders::update_validator_list_balance(
                        p, arg(), arg(), arg(), arg(), &validators, &[], 2, true,
                    ),
                ),
                one(
                    "update_validator_list_balance_chunk",
                    builders::update_validator_list_balance_chunk(
                        p, arg(), arg(), arg(), arg(), &validators, 0, 2, true,
                    )
                    .unwrap(),
                ),
            ],
        },
        Spec {
            encoded: unit!(UpdateStakePoolBalance),
            built: vec![one(
                "update_stake_pool_balance",
                builders::update_stake_pool_balance(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                ),
            )],
        },
        Spec {
            encoded: unit!(CleanupRemovedValidatorEntries),
            built: vec![one(
                "cleanup_removed_validator_entries",
                builders::cleanup_removed_validator_entries(p, arg(), arg()),
            )],
        },
        deposit_stake(p, false),
        withdraw_stake(p, false),
        Spec {
            encoded: unit!(SetManager),
            built: vec![one("set_manager", builders::set_manager(p, arg(), arg(), arg(), arg()))],
        },
        Spec {
            encoded: fields!(SetFee { fee: Val::FeeType(FeeType::Epoch(fee(100, 3))) }),
            built: vec![one(
                "set_fee",
                builders::set_fee(p, arg(), arg(), FeeType::Epoch(fee(100, 3))),
            )],
        },
        Spec {
            encoded: unit!(SetStaker),
            built: vec![one("set_staker", builders::set_staker(p, arg(), arg(), arg()))],
        },
        deposit_sol(p, false),
        Spec {
            encoded: tuple!(SetFundingAuthority(Val::FundingType(FundingType::SolWithdraw))),
            built: vec![
                one("set_funding_authority", set_funding_authority(None)),
                one("set_funding_authority", set_funding_authority(Some(&key()))),
            ],
        },
        withdraw_sol(p, false),
        token_metadata(p, false),
        token_metadata(p, true),
        Spec {
            encoded: fields!(IncreaseAdditionalValidatorStake {
                lamports: Val::U64(A),
                transient_stake_seed: Val::U64(B),
                ephemeral_stake_seed: Val::U64(C),
            }),
            built: vec![one(
                "increase_additional_validator_stake",
                builders::increase_additional_validator_stake(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B, C,
                ),
            )],
        },
        Spec {
            encoded: fields!(DecreaseAdditionalValidatorStake {
                lamports: Val::U64(A),
                transient_stake_seed: Val::U64(B),
                ephemeral_stake_seed: Val::U64(C),
            }),
            built: vec![one(
                "decrease_additional_validator_stake",
                builders::decrease_additional_validator_stake(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B, C,
                ),
            )],
        },
        Spec {
            encoded: fields!(DecreaseValidatorStakeWithReserve {
                lamports: Val::U64(A),
                transient_stake_seed: Val::U64(B),
            }),
            built: vec![one(
                "decrease_validator_stake_with_reserve",
                builders::decrease_validator_stake_with_reserve(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B,
                ),
            )],
        },
        Spec {
            encoded: fields!(Redelegate {
                lamports: Val::U64(A),
                source_transient_stake_seed: Val::U64(B),
                ephemeral_stake_seed: Val::U64(C),
                destination_transient_stake_seed: Val::U64(D),
            }),
            built: vec![one(
                "redelegate",
                builders::redelegate(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    A, B, C, D,
                ),
            )],
        },
        deposit_stake(p, true),
        withdraw_stake(p, true),
        deposit_sol(p, true),
        withdraw_sol(p, true),
    ]
}

/// A pool's default stake deposit authority is an address the builder derives and the
/// program signs for; a custom one is passed in and signs the transaction itself.
fn deposit_stake(p: &Pubkey, slippage: bool) -> Spec {
    let stake_pool = || {
        let stake_pool = key();
        derived(
            "stake_pool_deposit_authority",
            find_deposit_authority_program_address(p, &stake_pool).0,
        );
        pass(Box::leak(Box::new(stake_pool)))
    };
    if slippage {
        Spec {
            encoded: fields!(DepositStakeWithSlippage { minimum_pool_tokens_out: Val::U64(B) }),
            built: vec![
                own(p, "deposit_stake_with_slippage", builders::deposit_stake_with_slippage(
                    p, stake_pool(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    arg(), arg(), B,
                )),
                own(
                    p,
                    "deposit_stake_with_authority_and_slippage",
                    builders::deposit_stake_with_authority_and_slippage(
                        p, stake_pool(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                        arg(), arg(), arg(), arg(), B,
                    ),
                ),
            ],
        }
    } else {
        Spec {
            encoded: unit!(DepositStake),
            built: vec![
                own(p, "deposit_stake", builders::deposit_stake(
                    p, stake_pool(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    arg(), arg(),
                )),
                own(p, "deposit_stake_with_authority", builders::deposit_stake_with_authority(
                    p, stake_pool(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    arg(), arg(), arg(),
                )),
            ],
        }
    }
}

/// These two builders take no withdraw authority or metadata account: they derive both.
fn token_metadata(p: &Pubkey, update: bool) -> Spec {
    let (stake_pool, mint) = (key(), key());
    derived(
        "stake_pool_withdraw_authority",
        find_withdraw_authority_program_address(p, &stake_pool).0,
    );
    derived("token_metadata", find_metadata_account(&mint).0);
    let (name, symbol, uri) = ("name", "sym", "uri");
    let text = |value: &str| value.to_string();
    if update {
        Spec {
            encoded: fields!(UpdateTokenMetadata {
                name: Val::Str(name),
                symbol: Val::Str(symbol),
                uri: Val::Str(uri),
            }),
            built: vec![one(
                "update_token_metadata",
                builders::update_token_metadata(
                    p, pass(&stake_pool), arg(), pass(&mint), text(name), text(symbol), text(uri),
                ),
            )],
        }
    } else {
        Spec {
            encoded: fields!(CreateTokenMetadata {
                name: Val::Str(name),
                symbol: Val::Str(symbol),
                uri: Val::Str(uri),
            }),
            built: vec![one(
                "create_token_metadata",
                builders::create_token_metadata(
                    p, pass(&stake_pool), arg(), pass(&mint), arg(), text(name), text(symbol),
                    text(uri),
                ),
            )],
        }
    }
}

fn withdraw_stake(p: &Pubkey, slippage: bool) -> Spec {
    if slippage {
        Spec {
            encoded: fields!(WithdrawStakeWithSlippage {
                pool_tokens_in: Val::U64(A),
                minimum_lamports_out: Val::U64(B),
            }),
            built: vec![one(
                "withdraw_stake_with_slippage",
                builders::withdraw_stake_with_slippage(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    A, B,
                ),
            )],
        }
    } else {
        Spec {
            encoded: tuple!(WithdrawStake(Val::U64(A))),
            built: vec![one(
                "withdraw_stake",
                builders::withdraw_stake(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                    A,
                ),
            )],
        }
    }
}

fn deposit_sol(p: &Pubkey, slippage: bool) -> Spec {
    if slippage {
        Spec {
            encoded: fields!(DepositSolWithSlippage {
                lamports_in: Val::U64(A),
                minimum_pool_tokens_out: Val::U64(B),
            }),
            built: vec![
                one("deposit_sol_with_slippage", builders::deposit_sol_with_slippage(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B,
                )),
                one(
                    "deposit_sol_with_authority_and_slippage",
                    builders::deposit_sol_with_authority_and_slippage(
                        p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                        A, B,
                    ),
                ),
            ],
        }
    } else {
        Spec {
            encoded: tuple!(DepositSol(Val::U64(A))),
            built: vec![
                one("deposit_sol", builders::deposit_sol(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A,
                )),
                one("deposit_sol_with_authority", builders::deposit_sol_with_authority(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A,
                )),
            ],
        }
    }
}

fn withdraw_sol(p: &Pubkey, slippage: bool) -> Spec {
    if slippage {
        Spec {
            encoded: fields!(WithdrawSolWithSlippage {
                pool_tokens_in: Val::U64(A),
                minimum_lamports_out: Val::U64(B),
            }),
            built: vec![
                one("withdraw_sol_with_slippage", builders::withdraw_sol_with_slippage(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A, B,
                )),
                one(
                    "withdraw_sol_with_authority_and_slippage",
                    builders::withdraw_sol_with_authority_and_slippage(
                        p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(),
                        A, B,
                    ),
                ),
            ],
        }
    } else {
        Spec {
            encoded: tuple!(WithdrawSol(Val::U64(A))),
            built: vec![
                one("withdraw_sol", builders::withdraw_sol(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A,
                )),
                one("withdraw_sol_with_authority", builders::withdraw_sol_with_authority(
                    p, arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), arg(), A,
                )),
            ],
        }
    }
}

fn lower_camel(name: &str) -> String {
    let mut out = String::new();
    let mut upper = false;
    for (i, c) in name.chars().enumerate() {
        if c == '_' {
            upper = true;
        } else if i == 0 {
            out.extend(c.to_lowercase());
        } else if upper {
            out.extend(c.to_uppercase());
            upper = false;
        } else {
            out.push(c);
        }
    }
    out
}

/// One account position, merged over every builder shape of the instruction.
fn accounts(spec: &Spec, docs: &[String]) -> Vec<Value> {
    let variant = spec.encoded.variant;
    let length = spec.built.iter().map(|built| built.instruction.accounts.len()).max().unwrap();
    let mut nodes = Vec::new();
    let mut optional_seen = false;
    for position in 0..length {
        let present: Vec<_> = spec
            .built
            .iter()
            .filter_map(|built| built.instruction.accounts.get(position).map(|meta| (built, meta)))
            .collect();
        let (first_builder, first) = present[0];
        let mut name: Option<String> = None;
        let mut default: Option<&str> = None;
        let mut signs = false;
        let mut declines = false;
        for (built, meta) in &present {
            assert_eq!(
                meta.is_writable, first.is_writable,
                "{variant} account {position}: {} and {} disagree on writable",
                first_builder.function, built.function
            );
            if meta.is_signer {
                signs = true;
            } else {
                declines = true;
            }
            let address = meta.pubkey.to_string();
            let (this_name, this_default) =
                match CONSTANTS.iter().find(|(constant, _)| *constant == address) {
                    Some((constant, name)) => (name.to_string(), Some(*constant)),
                    None => {
                        let name = built.names.get(&meta.pubkey).cloned().or_else(|| {
                            DERIVED.with(|derived| {
                                derived.borrow().get(&meta.pubkey).map(|name| name.to_string())
                            })
                        });
                        let name = name.unwrap_or_else(|| {
                            panic!(
                                "{variant} account {position}: {} sends {address}, which it was \
                                 not passed and this tool does not name",
                                built.function
                            )
                        });
                        (lower_camel(&name), None)
                    }
                };
            if let Some(name) = &name {
                assert_eq!(
                    (name.as_str(), default),
                    (this_name.as_str(), this_default),
                    "{variant} account {position}: builders disagree on what the account is"
                );
            }
            name = Some(this_name);
            default = this_default;
        }
        let optional = present.len() < spec.built.len();
        assert!(
            optional || !optional_seen,
            "{variant}: account {position} follows an optional one"
        );
        optional_seen |= optional;

        let mut node = Map::new();
        node.insert("kind".into(), json!("instructionAccountNode"));
        node.insert("name".into(), json!(name.unwrap()));
        node.insert("isWritable".into(), json!(first.is_writable));
        let signer = if signs && declines { json!("either") } else { json!(signs) };
        node.insert("isSigner".into(), signer);
        node.insert("isOptional".into(), json!(optional));
        if let Some(doc) = docs.get(position).filter(|doc| !doc.is_empty()) {
            node.insert("docs".into(), json!([doc]));
        }
        if let Some(default) = default {
            node.insert(
                "defaultValue".into(),
                json!({"kind": "publicKeyValueNode", "publicKey": default}),
            );
        }
        nodes.push(Value::Object(node));
    }
    nodes
}

/// Upstream's doc comment on one enum variant, split into what it says about the
/// instruction, about each account, and about each field.
#[derive(Default)]
struct VariantDocs {
    description: Vec<String>,
    /// The numbered account list, without its numbers and flag markers, up to the first
    /// entry that documents a range of accounts: what follows a range has no fixed position.
    accounts: Vec<String>,
    /// What the first range entry says follows the fixed accounts.
    range: Option<String>,
    /// The variant's `#[deprecated]` attribute, which is not part of its doc comment.
    deprecated: Option<String>,
    fields: HashMap<String, String>,
}

fn doc_text(line: &str) -> Option<&str> {
    let line = line.trim_start();
    line.strip_prefix("///").map(|text| text.strip_prefix(' ').unwrap_or(text))
}

fn paragraphs(lines: &[String]) -> Vec<String> {
    let mut out = Vec::new();
    let mut current = String::new();
    for line in lines {
        let line = line.trim();
        if line.is_empty() {
            if !current.is_empty() {
                out.push(std::mem::take(&mut current));
            }
        } else {
            if !current.is_empty() {
                current.push(' ');
            }
            current.push_str(line);
        }
    }
    if !current.is_empty() {
        out.push(current);
    }
    out
}

/// ``0. `[w]` New pool`` is an item; ``7. `..7+2N` [] pairs`` is a range of them. The
/// marker is one or more bracketed flag groups joined by `/`, in backticks or apostrophes
/// that upstream does not always pair.
fn account_item(line: &str) -> Option<(bool, String)> {
    const QUOTES: [char; 2] = ['`', '\''];
    let line = line.trim();
    let digits = line.chars().take_while(char::is_ascii_digit).count();
    if digits == 0 || !line[digits..].starts_with(". ") {
        return None;
    }
    let mut rest = line[digits + 1..].trim_start();
    let range = rest.trim_start_matches(QUOTES).starts_with("..");
    if range {
        rest = rest.split_once(char::is_whitespace).map_or("", |(_, rest)| rest).trim_start();
    }
    let mut marker = rest.trim_start_matches(QUOTES);
    if marker.starts_with('[') {
        while let Some((_, after)) = marker.strip_prefix('[').and_then(|group| group.split_once(']'))
        {
            marker = after;
            match marker.strip_prefix('/') {
                Some(next) if next.starts_with('[') => marker = next,
                _ => break,
            }
        }
        rest = marker.trim_start_matches(QUOTES).trim_start();
    }
    assert!(
        !rest.starts_with(['`', '\'', '/', '[']),
        "the flag marker of this account line was not understood: {line}"
    );
    Some((range, rest.to_string()))
}

fn variant_docs(lines: Vec<String>, deprecated: Option<String>) -> VariantDocs {
    let mut description: Vec<String> = Vec::new();
    let mut items: Vec<String> = Vec::new();
    let mut range: Option<String> = None;
    let mut in_item = false;
    for line in lines {
        if let Some((is_range, text)) = account_item(&line) {
            if is_range && range.is_none() {
                range = Some(text);
            } else if range.is_none() {
                items.push(text);
            }
            in_item = true;
        } else if line.trim().is_empty() {
            in_item = false;
            description.push(String::new());
        } else if in_item {
            if range.is_none() {
                let last = items.last_mut().unwrap();
                last.push(' ');
                last.push_str(line.trim());
            }
        } else {
            description.push(line);
        }
    }
    VariantDocs {
        description: paragraphs(&description),
        accounts: items,
        range,
        deprecated,
        fields: HashMap::new(),
    }
}

/// `since = "2.0.0"` gives `2.0.0`.
fn attribute_value<'a>(attribute: &'a str, name: &str) -> Option<&'a str> {
    let after = attribute.split_once(&format!("{name} = \""))?.1;
    Some(after.split_once('"')?.0)
}

/// The doc comments of an enum's variants and of their named fields, in source order.
fn enum_docs(source: &str, enum_name: &str) -> Vec<(String, VariantDocs)> {
    let opening = format!("pub enum {enum_name} {{");
    let mut lines = source.lines().skip_while(|line| line.trim() != opening).skip(1);
    let mut out: Vec<(String, VariantDocs)> = Vec::new();
    let mut pending: Vec<String> = Vec::new();
    let mut deprecated: Option<String> = None;
    let mut attribute = String::new();
    let mut attribute_depth = 0i32;
    let mut in_fields = false;
    for line in &mut lines {
        if line == "}" {
            return out;
        }
        let trimmed = line.trim();
        let indent = line.len() - line.trim_start().len();
        if attribute_depth > 0 || trimmed.starts_with("#[") {
            attribute.push_str(trimmed);
            attribute.push(' ');
            attribute_depth += trimmed.matches(['(', '[']).count() as i32;
            attribute_depth -= trimmed.matches([')', ']']).count() as i32;
            if attribute_depth == 0 {
                if attribute.starts_with("#[deprecated") && indent == 4 {
                    let since = attribute_value(&attribute, "since").unwrap_or("an unstated version");
                    let note = attribute_value(&attribute, "note").unwrap_or("no note given");
                    deprecated = Some(format!("Deprecated since {since}: {note}"));
                }
                attribute.clear();
            }
        } else if let Some(text) = doc_text(line) {
            pending.push(text.to_string());
        } else if trimmed.is_empty() || trimmed.starts_with("//") {
            // a blank line, or an ordinary comment such as the numbering in the error enum
        } else if indent == 4 && trimmed.starts_with(|c: char| c.is_ascii_uppercase()) {
            let name: String =
                trimmed.chars().take_while(|c| c.is_ascii_alphanumeric()).collect();
            out.push((name, variant_docs(std::mem::take(&mut pending), deprecated.take())));
            in_fields = trimmed.ends_with('{');
        } else if in_fields && indent == 8 && trimmed.contains(':') {
            let field = trimmed.split(':').next().unwrap().trim().to_string();
            let text = paragraphs(&std::mem::take(&mut pending)).join(" ");
            out.last_mut().unwrap().1.fields.insert(field, text);
        } else {
            in_fields = false;
            pending.clear();
        }
    }
    panic!("the end of enum {enum_name} was not found");
}

/// The doc comment above `pub struct Name` or `pub enum Name`.
fn type_docs(sources: &[&str], name: &str) -> Vec<String> {
    for source in sources {
        let lines: Vec<&str> = source.lines().collect();
        let declared = lines.iter().position(|line| {
            *line == format!("pub struct {name} {{") || *line == format!("pub enum {name} {{")
        });
        if let Some(mut at) = declared {
            let mut docs = Vec::new();
            while at > 0 {
                at -= 1;
                match doc_text(lines[at]) {
                    Some(text) => docs.insert(0, text.to_string()),
                    None if docs.is_empty() && !lines[at].trim().is_empty() => continue,
                    None => break,
                }
            }
            return paragraphs(&docs);
        }
    }
    panic!("the declaration of {name} was not found");
}

fn schema_type(declaration: &str) -> Value {
    match declaration {
        "u8" | "u16" | "u32" | "u64" => number(declaration),
        "bool" => json!({"kind": "booleanTypeNode", "size": number("u8")}),
        "Fee" | "FeeType" | "FundingType" | "PreferredValidatorType" => link(declaration),
        other => panic!("no IDL type is known for the Borsh declaration {other}"),
    }
}

/// A defined type, from the Borsh schema the crate derives for it.
fn defined_type<T: BorshSchema>(docs: Vec<String>) -> Value {
    let schema = BorshSchemaContainer::for_type::<T>();
    let name = schema.declaration().clone();
    let type_node = match schema.get_definition(&name).unwrap() {
        Definition::Struct { fields: Fields::NamedFields(fields) } => json!({
            "kind": "structTypeNode",
            "fields": fields.iter().map(|(field, declaration)| json!({
                "kind": "structFieldTypeNode",
                "name": lower_camel(field),
                "type": schema_type(declaration),
            })).collect::<Vec<_>>(),
        }),
        Definition::Enum { tag_width, variants } => {
            assert_eq!(*tag_width, 1, "{name}: only a one-byte enum tag is described");
            let variants: Vec<Value> = variants
                .iter()
                .enumerate()
                .map(|(index, (discriminant, variant, declaration))| {
                    assert_eq!(*discriminant, index as i64, "{name}::{variant} is out of order");
                    let variant = lower_camel(variant);
                    match schema.get_definition(declaration).unwrap() {
                        Definition::Struct { fields: Fields::Empty } => {
                            json!({"kind": "enumEmptyVariantTypeNode", "name": variant})
                        }
                        Definition::Struct { fields: Fields::UnnamedFields(items) } => json!({
                            "kind": "enumTupleVariantTypeNode",
                            "name": variant,
                            "tuple": {
                                "kind": "tupleTypeNode",
                                "items": items.iter().map(|item| schema_type(item))
                                    .collect::<Vec<_>>(),
                            },
                        }),
                        other => panic!("{name}::{variant}: unsupported variant shape {other:?}"),
                    }
                })
                .collect();
            json!({"kind": "enumTypeNode", "variants": variants, "size": number("u8")})
        }
        other => panic!("{name}: unsupported Borsh definition {other:?}"),
    };
    let mut node = Map::new();
    node.insert("kind".into(), json!("definedTypeNode"));
    node.insert("name".into(), json!(lower_camel(&name)));
    if !docs.is_empty() {
        node.insert("docs".into(), json!(docs));
    }
    node.insert("type".into(), type_node);
    Value::Object(node)
}

/// The lock file this binary was built under. It names the crate that was linked.
const LOCK: &str = include_str!("../Cargo.lock");

/// Where cargo unpacked the crate this tool is linked against, its version, and the
/// checksum the lock file holds it to.
fn crate_source() -> (PathBuf, String, String) {
    let manifest_dir = Path::new(env!("CARGO_MANIFEST_DIR"));
    let lock = fs::read_to_string(manifest_dir.join("Cargo.lock")).expect("Cargo.lock");
    assert!(lock == LOCK, "Cargo.lock changed since this binary was built; run it through cargo");

    let entry = LOCK
        .split("[[package]]")
        .find(|entry| entry.contains("name = \"spl-stake-pool\""))
        .expect("Cargo.lock has no spl-stake-pool");
    let value = |key: &str| {
        entry
            .lines()
            .find_map(|line| line.strip_prefix(key))
            .map(|value| value.trim_matches('"').to_string())
            .unwrap_or_else(|| panic!("Cargo.lock holds no `{key}` for spl-stake-pool"))
    };
    let (version, checksum) = (value("version = "), value("checksum = "));

    let output = Command::new(env!("CARGO"))
        .args(["metadata", "--format-version", "1", "--locked", "--manifest-path"])
        .arg(manifest_dir.join("Cargo.toml"))
        .output()
        .expect("cargo metadata did not run");
    assert!(output.status.success(), "{}", String::from_utf8_lossy(&output.stderr));
    let metadata: Value = serde_json::from_slice(&output.stdout).unwrap();
    let package = metadata["packages"]
        .as_array()
        .unwrap()
        .iter()
        .find(|package| package["name"] == "spl-stake-pool")
        .expect("spl-stake-pool is not among the resolved packages");
    assert_eq!(package["version"], version.as_str(), "cargo metadata and Cargo.lock disagree");
    let manifest = Path::new(package["manifest_path"].as_str().unwrap());
    (manifest.parent().unwrap().join("src"), version, checksum)
}

/// The names of an instruction's arguments. A struct variant's are its field names. A tuple
/// variant's fields each take the name of the builder parameter that feeds them: the
/// parameters of the field's type, in order, one per field, in every builder of the
/// instruction and under the same names.
fn argument_names(spec: &Spec) -> Vec<String> {
    let variant = spec.encoded.variant;
    if spec.encoded.args.iter().all(|(name, _)| name.is_some()) {
        return spec.encoded.args.iter().map(|(name, _)| name.unwrap().to_string()).collect();
    }
    let mut names: Option<Vec<String>> = None;
    for built in &spec.built {
        let mut parameters = signature(built.function);
        let found: Vec<String> = spec
            .encoded
            .args
            .iter()
            .map(|(_, value)| {
                let at = parameters
                    .iter()
                    .position(|(_, kind)| value.parameter_types().contains(&kind.as_str()))
                    .unwrap_or_else(|| {
                        panic!("{variant}: {} has no parameter for {value:?}", built.function)
                    });
                parameters.remove(at).0
            })
            .collect();
        for (_, value) in &spec.encoded.args {
            assert!(
                !parameters.iter().any(|(_, kind)| value.parameter_types().contains(&kind.as_str())),
                "{variant}: {} has more parameters that could feed {value:?} than fields",
                built.function
            );
        }
        if let Some(names) = &names {
            assert_eq!(*names, found, "{variant}: its builders name the fields differently");
        }
        names = Some(found);
    }
    names.unwrap()
}

fn derive() -> String {
    let program = spl_stake_pool::id();
    let (source, version, checksum) = crate_source();
    let read = |file: &str| {
        fs::read_to_string(source.join(file))
            .unwrap_or_else(|error| panic!("{}: {error}", source.join(file).display()))
    };
    let (instruction_rs, error_rs, state_rs) =
        (read("instruction.rs"), read("error.rs"), read("state.rs"));

    read_signatures(&instruction_rs);
    let specs = specs(&program);
    let docs = enum_docs(&instruction_rs, "StakePoolInstruction");
    assert_eq!(docs.len(), specs.len(), "the enum documents {} variants", docs.len());

    let mut instructions = Vec::new();
    for (index, (spec, (documented, docs))) in specs.iter().zip(&docs).enumerate() {
        let variant = spec.encoded.variant;
        assert_eq!(documented, variant, "variant {index} of the enum");
        assert_eq!(spec.encoded.data[0] as usize, index, "{variant} is not variant {index}");
        for built in &spec.built {
            assert_eq!(
                built.instruction.data, spec.encoded.data,
                "{} did not serialize {variant} with the arguments declared for it",
                built.function
            );
        }
        let mut layout = vec![index as u8];
        for (_, value) in &spec.encoded.args {
            value.encode(&mut layout);
        }
        assert_eq!(
            layout, spec.encoded.data,
            "{variant}: its arguments, in the order and of the types listed here, are not the \
             bytes the enum serializes"
        );
        let argument_names = argument_names(spec);

        let length = spec.built.iter().map(|built| built.instruction.accounts.len()).max().unwrap();
        let account_docs = if docs.accounts.len() == length {
            docs.accounts.clone()
        } else {
            eprintln!(
                "{variant}: upstream documents {} accounts and its builders send {length}; \
                 no account docs carried",
                docs.accounts.len()
            );
            Vec::new()
        };
        let accounts = accounts(spec, &account_docs);

        let mut arguments = vec![json!({
            "kind": "instructionArgumentNode",
            "name": "discriminator",
            "defaultValueStrategy": "omitted",
            "type": number("u8"),
            "defaultValue": {"kind": "numberValueNode", "number": index},
        })];
        for ((_, value), name) in spec.encoded.args.iter().zip(&argument_names) {
            let mut argument = Map::new();
            argument.insert("kind".into(), json!("instructionArgumentNode"));
            argument.insert("name".into(), json!(lower_camel(name)));
            argument.insert("type".into(), value.type_node());
            if let Some(doc) = docs.fields.get(name).filter(|doc| !doc.is_empty()) {
                argument.insert("docs".into(), json!([doc]));
            }
            arguments.push(Value::Object(argument));
        }

        let mut node = Map::new();
        node.insert("kind".into(), json!("instructionNode"));
        node.insert("name".into(), json!(lower_camel(variant)));
        if accounts.iter().any(|account| account["isOptional"] == true) {
            node.insert("optionalAccountStrategy".into(), json!("omitted"));
        }
        let mut instruction_docs: Vec<String> = docs.deprecated.iter().cloned().collect();
        instruction_docs.extend(docs.description.iter().cloned());
        if let Some(range) = &docs.range {
            instruction_docs.push(format!("Followed by: {range}"));
        }
        if !instruction_docs.is_empty() {
            node.insert("docs".into(), json!(instruction_docs));
        }
        node.insert("accounts".into(), json!(accounts));
        node.insert("arguments".into(), json!(arguments));
        node.insert(
            "discriminators".into(),
            json!([{"kind": "fieldDiscriminatorNode", "name": "discriminator", "offset": 0}]),
        );
        instructions.push(Value::Object(node));
    }

    let sources = [state_rs.as_str(), instruction_rs.as_str()];
    let defined_types = vec![
        defined_type::<Fee>(type_docs(&sources, "Fee")),
        defined_type::<FeeType>(type_docs(&sources, "FeeType")),
        defined_type::<PreferredValidatorType>(type_docs(&sources, "PreferredValidatorType")),
        defined_type::<FundingType>(type_docs(&sources, "FundingType")),
    ];

    let error_docs = enum_docs(&error_rs, "StakePoolError");
    let mut errors = Vec::new();
    while let Some(error) = StakePoolError::from_u32(errors.len() as u32) {
        let code = errors.len();
        let name = format!("{error:?}");
        let (documented, docs) = error_docs
            .get(code)
            .unwrap_or_else(|| panic!("error {code} ({name}) has no entry in error.rs"));
        assert_eq!(*documented, name, "error {code}");
        let message = docs.description.join(" ");
        errors.push(json!({
            "kind": "errorNode",
            "name": lower_camel(&name),
            "code": code,
            "message": if message.is_empty() { error.to_string() } else { message },
        }));
    }
    assert_eq!(errors.len(), error_docs.len(), "error.rs documents {} errors", error_docs.len());

    let root = json!({
        "kind": "rootNode",
        "standard": "codama",
        "version": "1.0.0",
        "program": {
            "kind": "programNode",
            "name": "stakePool",
            "publicKey": program.to_string(),
            "version": version,
            // Upstream uses no Shank. This is what tells idl-src-gen that instructions
            // dispatch on a one-byte ordinal and that no account discriminator exists.
            "origin": "shank",
            "docs": [
                "SPL Stake Pool. The one-byte discriminator is the index of the instruction in \
                 StakePoolInstruction, and arguments are Borsh serialized.",
                format!(
                    "Derived by tools/stake-pool-idl from the spl-stake-pool crate {version} \
                     (crates.io checksum {checksum}). Not edited by hand: change the tool or the \
                     crate version and derive again."
                ),
            ],
            "instructions": instructions,
            "definedTypes": defined_types,
            "accounts": [],
            "errors": errors,
        },
    });
    let mut text = serde_json::to_string_pretty(&root).unwrap();
    text.push('\n');
    text
}

fn main() {
    let arguments: Vec<String> = std::env::args().skip(1).collect();
    let (check, path) = match arguments.as_slice() {
        [path] if path != "--check" => (false, path),
        [flag, path] if flag == "--check" => (true, path),
        _ => {
            eprintln!("usage: stake-pool-idl [--check] <path to idls/spl_stake_pool.json>");
            exit(2);
        }
    };
    let derived = derive();
    if check {
        let on_disk = fs::read_to_string(path).unwrap_or_else(|error| panic!("{path}: {error}"));
        if on_disk != derived {
            eprintln!("{path} is not what this tool derives; derive it again, do not edit it");
            exit(1);
        }
        eprintln!("{path} is what this tool derives");
    } else {
        fs::write(path, derived).unwrap_or_else(|error| panic!("{path}: {error}"));
        eprintln!("wrote {path}");
    }
}
