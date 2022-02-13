package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.entity.EvidenceEncrypted;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Arrays;

public class Processor implements ItemProcessor<Evidence, EvidenceEncrypted> {

    @Autowired
    Key key;

    @Override
    public EvidenceEncrypted process(Evidence evidence) throws Exception {
        /*
            cifratura simmetrica a blocchi con CBC , si necessita del blocco di inizializzazione IV
         */
        EvidenceEncrypted evidenceEncrypted = new EvidenceEncrypted();
        byte[] iv = generateInitVector();
        evidenceEncrypted.setId(evidence.getId());
        evidenceEncrypted.setFirst_name(encrypt(key, iv, evidence.first_name.getBytes()));
        evidenceEncrypted.setLast_name(encrypt(key, iv, evidence.last_name.getBytes()));
        evidenceEncrypted.setEmail(encrypt(key, iv, evidence.email.getBytes()));
        evidenceEncrypted.setGender(encrypt(key, iv, evidence.gender.getBytes()));
        evidenceEncrypted.setIp_address(encrypt(key, iv, evidence.ip_address.getBytes()));

        return evidenceEncrypted;
    }

    public static byte [] encrypt( Key key, byte[] iv, byte[] plaintext ) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, key, new IvParameterSpec( iv ) );
        return cipher.doFinal( plaintext );
    }

    public static byte [] generateInitVector() {
        Integer SYM_KEY_SIZE = 128;
        SecureRandom random = new SecureRandom();
        byte [] iv = new byte [ SYM_KEY_SIZE  / 8 ];
        random.nextBytes( iv );
        return iv;
    }

}