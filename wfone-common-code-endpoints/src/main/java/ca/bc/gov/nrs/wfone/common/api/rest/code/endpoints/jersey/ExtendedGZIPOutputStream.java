package ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.jersey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Basic extension of GZIP output stream for adding a "setLevel"
 * Used in the Writer Interceptor for setting compression levels.
 * See java.util.zip.Deflater
 */
public class ExtendedGZIPOutputStream extends GZIPOutputStream {
  public ExtendedGZIPOutputStream(OutputStream out) throws IOException {
    super(out);
  }

  public void setLevel(int level) {
      def.setLevel(level);
  }
}
