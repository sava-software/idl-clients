package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.encoding.Base58;
import software.sava.core.tx.TransactionSkeleton;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram.AuthorizeIxData;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram.SplitIxData;
import software.sava.idl.clients.spl.stake.gen.types.StakeAuthorize;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.system.gen.SystemProgram.AllocateWithSeedIxData;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class StakeProgramTests {

  @Test
  void testParseSplitIx() {
    final var txData = Base64.getDecoder().decode("""
        AfPMO/XQqFumySc8qFm5FzYtO/tZvZwOhh5MszXWmBMPZ9Dtdu8kIUGRjs1SFsv/EK5z1XM9zRVrY+SARus9VQIBAAIFQGApaPSUd2ZTMXfwxK/W6eXjSD7N7MsyiFXUZkYeJzeZHGihq3yZZZuANsM2GDQP6WW4s0evzODV4t9B9k7rbuAZyl2EfWaA7HjlkK2cDUbFtN4Qdkmc3UdLplpvaYIaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGodgXkTdUKpg0N73+KnqyVX9TXIp4citopJ3AAAAAAL+0lv5+ZSV0libhfO9SW3Vrk6n1HvV9uLZQVHwPQ8cMAgMCAgBbCQAAAEBgKWj0lHdmUzF38MSv1unl40g+zezLMohV1GZGHic3BwAAAAAAAABzdGFrZTo5yAAAAAAAAAAGodgXkTdUKpg0N73+KnqyVX9TXIp4citopJ3AAAAAAAQDAQIADAMAAAAAvKXf2AMAAA==
        """.stripTrailing());

    var skeleton = TransactionSkeleton.deserializeSkeleton(txData);
    var instructions = skeleton.parseLegacyInstructions();
    assertEquals(2, instructions.length);

    var allocateWithSeedIx = instructions[0];
    var allocateWithSeedData = AllocateWithSeedIxData.read(allocateWithSeedIx);
    assertArrayEquals(SystemProgram.ALLOCATE_WITH_SEED_DISCRIMINATOR.data(), discriminatorBytes(allocateWithSeedData.discriminator()));
    assertEquals(PublicKey.fromBase58Encoded("5LJ93G4SQh9GiewTQJNAu6X9sQ1VVyrpCAgbQsRSgn22"), allocateWithSeedData.base());
    assertEquals("stake:9", allocateWithSeedData.seed());
    assertEquals(200, allocateWithSeedData.space());
    assertEquals(PublicKey.fromBase58Encoded("Stake11111111111111111111111111111111111111"), allocateWithSeedData.programAddress());

    var splitIx = instructions[1];
    var splitData = SplitIxData.read(splitIx);
    assertArrayEquals(SolanaStakeInterfaceProgram.SPLIT_DISCRIMINATOR.data(), discriminatorBytes(splitData.discriminator()));
    assertEquals(4230000000000L, splitData.args());
  }

  @Test
  void authorizeData() {
    byte[] data = Base58.decode("3t9dD1DMBKjQBnMKfD6zcqBgYJE8LDDw42WTR29f8AVNx6xfDrMJJK");
    var authorize = AuthorizeIxData.read(data, 0);
    assertArrayEquals(SolanaStakeInterfaceProgram.AUTHORIZE_DISCRIMINATOR.data(), discriminatorBytes(authorize.discriminator()));
    assertEquals("4zeVNswbjb8x2FnEkGpmuhUQbPLR4MB4ZKj4NNrz5KeC", authorize.arg0().toBase58());
    assertEquals(StakeAuthorize.staker, authorize.arg1());

    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    var ix = SolanaStakeInterfaceProgram.authorize(
        solanaAccounts.invokedStakeProgram(),
        List.of(),
        authorize.arg0(),
        StakeAuthorize.staker
    );
    assertArrayEquals(data, ix.data());

    data = Base58.decode("3t9dD1DMBKjQBnMKfD6zcqBgYJE8LDDw42WTR29f8AVNx6xfDsqHaf");
    authorize = AuthorizeIxData.read(data, 0);
    assertArrayEquals(SolanaStakeInterfaceProgram.AUTHORIZE_DISCRIMINATOR.data(), discriminatorBytes(authorize.discriminator()));
    assertEquals("4zeVNswbjb8x2FnEkGpmuhUQbPLR4MB4ZKj4NNrz5KeC", authorize.arg0().toBase58());
    assertEquals(StakeAuthorize.withdrawer, authorize.arg1());

    ix = SolanaStakeInterfaceProgram.authorize(
        solanaAccounts.invokedStakeProgram(),
        List.of(),
        authorize.arg0(),
        StakeAuthorize.withdrawer
    );
    assertArrayEquals(data, ix.data());
  }

  private static byte[] discriminatorBytes(final long discriminator) {
    return new byte[]{
        (byte) discriminator,
        (byte) (discriminator >> 8),
        (byte) (discriminator >> 16),
        (byte) (discriminator >> 24)
    };
  }
}
