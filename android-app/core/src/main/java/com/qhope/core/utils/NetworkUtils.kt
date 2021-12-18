package com.qhope.core.utils

import android.content.Context
import com.qhope.core.R
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object NetworkUtils {

  @Throws(Exception::class)
  fun getSslContext(context: Context): Pair<SSLContext, X509TrustManager?> {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
      load(null, null)
    } // "BKS"
    val inputStream: InputStream = context.resources.openRawResource(R.raw.certificate)
//    val certificate: String = Converter.convertStreamToString(inputStream)
//
//    // generate input stream for certificate factory
//    val stream: InputStream = IOUtils.toInputStream(certificate)

    val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
    val certificate: Certificate = inputStream.use { stream ->
      certificateFactory.generateCertificate(stream)
    }
    keyStore.setCertificateEntry("certificate", certificate)

    // TrustManagerFactory
    val trustManagerFactory: TrustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(keyStore)

    // Create a SSLContext with the certificate that uses tmf (TrustManager)
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
    return Pair(sslContext, trustManagerFactory.trustManagers.getOrNull(0) as? X509TrustManager)
  }
}