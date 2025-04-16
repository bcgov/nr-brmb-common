package ca.bc.gov.nrs.wfone.common.rest.endpoints.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BufferedRequestWrapper extends HttpServletRequestWrapper {

		private BufferedReader br = null;
        private BufferedServletInputStream bsis = null;
        private byte[] buffer = null;
 

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);

            try(InputStream is = req.getInputStream()) {
            
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            byte b[] = new byte[1024];
	            int read;
	            while ((read = is.read(b)) > 0) {
	            	baos.write(b, 0, read);
	            }
	            this.buffer = baos.toByteArray();
            }
        }

        @Override
        public ServletInputStream getInputStream() {
        	if(this.br!=null) {
        		throw new IllegalStateException("getReader has already been called.");
        	} else if(this.bsis==null) {
        		this.bsis = new BufferedServletInputStream(new ByteArrayInputStream(this.buffer));
        	}
            return this.bsis;
        }
        
        @Override
        public BufferedReader getReader() {
        	if(this.bsis!=null) {
        		throw new IllegalStateException("getInputStream has already been called.");
        	} else if(this.br==null) {
        		this.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.buffer)));
        	}
            return this.br;
        }

        public String getRequestBody() throws IOException  {
            return new String(this.buffer, "UTF-8");
        }

    }