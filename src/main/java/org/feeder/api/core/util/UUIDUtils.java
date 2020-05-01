package org.feeder.api.core.util;

import static java.lang.System.arraycopy;
import static org.hibernate.internal.util.BytesHelper.fromLong;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import java.nio.ByteBuffer;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDUtils {

  private static final TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator();

  /**
   * <a href="https://mysqlserverteam.com/storing-uuid-values-in-mysql-tables">Refer to:</a>
   * <p>
   * MySQL’s UUID() uses version 1, which implies, as explained in paragraph 4.1.2 of the RFC, that
   * the three leftmost dash-separated groups are a 8-byte timestamp: leftmost group is the low four
   * bytes of the timestamp; second group is the middle two bytes, third group is the high (most
   * significant) two bytes of the timestamp. Thus the leftmost group varies the fastest (10 times
   * per microsecond).
   * <p>
   * So, in a sequence of UUIDs continuously generated by a single machine, all UUIDs have different
   * first bytes. Inserting this sequence into an indexed column (in binary or text  form) will thus
   * modify a different index page each time, preventing in-memory caching. So it makes sense to
   * re-arrange the UUID, making the rapidly-changing parts go last, before we store into id_bin.
   *
   * @return optimized UUID
   */
  public static UUID optimizedUUID() {
    return getGUIDFromByteArray(transform(timeBasedGenerator.generate()));
  }

  private static byte[] transform(final UUID uuid) {

    final byte[] out = new byte[16];
    final byte[] msbIn = fromLong(uuid.getMostSignificantBits());

    arraycopy(msbIn, 6, out, 0, 2);
    arraycopy(msbIn, 4, out, 2, 2);
    arraycopy(msbIn, 0, out, 4, 4);
    arraycopy(fromLong(uuid.getLeastSignificantBits()), 0, out, 8, 8);

    return out;
  }

  public static UUID getGUIDFromByteArray(final byte[] bytes) {
    final ByteBuffer bb = ByteBuffer.wrap(bytes);
    return new UUID(bb.getLong(), bb.getLong());
  }
}
