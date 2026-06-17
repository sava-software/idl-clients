package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class KaminoVaultPDAs {

  public static ProgramDerivedAddress baseVaultAuthority(final PublicKey vaultState, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "authority".getBytes(US_ASCII),
            vaultState.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress tokenVault(final PublicKey vaultState, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "token_vault".getBytes(US_ASCII),
            vaultState.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress sharesMint(final PublicKey vaultState, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "shares".getBytes(US_ASCII),
            vaultState.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress ctokenVault(final PublicKey vaultState,
                                                  final PublicKey reserve,
                                                  final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "ctoken_vault".getBytes(US_ASCII),
            vaultState.toByteArray(),
            reserve.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress whitelistedReserve(final PublicKey reserve, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "whitelisted_reserves".getBytes(US_ASCII),
            reserve.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress globalConfig(final PublicKey programId) {
    return PublicKey.findProgramAddress(List.of("global_config".getBytes(US_ASCII)), programId);
  }

  public static ProgramDerivedAddress eventAuthority(final PublicKey programId) {
    return PublicKey.findProgramAddress(List.of("__event_authority".getBytes(US_ASCII)), programId);
  }

  // [program_id] against the BPF upgradeable loader, i.e. the program's ProgramData account.
  public static ProgramDerivedAddress programData(final PublicKey programId, final PublicKey bpfLoaderUpgradeableProgram) {
    return PublicKey.findProgramAddress(List.of(programId.toByteArray()), bpfLoaderUpgradeableProgram);
  }

  // Metaplex token metadata PDA for the shares mint: [METADATA_SEEDS, token_metadata_program, shares_mint]
  public static ProgramDerivedAddress sharesMetadata(final PublicKey sharesMint, final PublicKey tokenMetadataProgram) {
    return PublicKey.findProgramAddress(
        List.of(
            "metadata".getBytes(US_ASCII),
            tokenMetadataProgram.toByteArray(),
            sharesMint.toByteArray()
        ),
        tokenMetadataProgram
    );
  }

  private KaminoVaultPDAs() {
  }
}
