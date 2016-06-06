package uk.gov.dwp.buckify.dependencies

import java.security.MessageDigest

class Checksum {
    static String generateSHA1(File f) {
        int KB = 1024
        int MB = 1024*KB

        if (!f.exists() || !f.isFile()) {
            throw new IllegalArgumentException("Invalid file $f")
        }

        def messageDigest = MessageDigest.getInstance("SHA1")

        f.eachByte(MB) { byte[] buf, int bytesRead ->
            messageDigest.update(buf, 0, bytesRead);
        }

        new BigInteger(1, messageDigest.digest()).toString(16).padLeft( 40, '0' ).toString()
    }
}
