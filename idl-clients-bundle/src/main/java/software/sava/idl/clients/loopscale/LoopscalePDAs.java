package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class LoopscalePDAs {

  public static ProgramDerivedAddress protocolAdminState(final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of("protocol_admin_state".getBytes(US_ASCII)),
        programId
    );
  }

  public static ProgramDerivedAddress eventAuthority(final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of("__event_authority".getBytes(US_ASCII)),
        programId
    );
  }

  public static ProgramDerivedAddress loan(final PublicKey borrower,
                                           final long nonce,
                                           final PublicKey programId) {
    final byte[] nonceBytes = new byte[Long.BYTES];
    ByteUtil.putInt64LE(nonceBytes, 0, nonce);
    return PublicKey.findProgramAddress(
        List.of(
            borrower.toByteArray(),
            nonceBytes
        ),
        programId
    );
  }

  public static ProgramDerivedAddress strategy(final PublicKey nonce, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "strategy".getBytes(US_ASCII),
            nonce.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress vault(final PublicKey vaultNonce, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "vault".getBytes(US_ASCII),
            vaultNonce.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress vaultStrategy(final PublicKey vault, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "strategy".getBytes(US_ASCII),
            vault.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress vaultRewardsInfo(final PublicKey vault, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "vault_rewards_info".getBytes(US_ASCII),
            vault.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress vaultStake(final PublicKey stakeNonce,
                                                 final PublicKey vault,
                                                 final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "vault_stake".getBytes(US_ASCII),
            stakeNonce.toByteArray(),
            vault.toByteArray()
        ),
        programId
    );
  }

  public static ProgramDerivedAddress userRewardsInfo(final PublicKey vaultStake, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "user_rewards_info".getBytes(US_ASCII),
            vaultStake.toByteArray()
        ),
        programId
    );
  }

  private LoopscalePDAs() {
  }
}
