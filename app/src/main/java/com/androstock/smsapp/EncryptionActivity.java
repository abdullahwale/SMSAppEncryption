package com.androstock.smsapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ow2.util.base64.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionActivity extends Activity {

    private Button encryptButton = null, decryptButton = null, clearButton = null;
    private EditText decryptedText = null, encryptedText = null, inputtedUnencryptedText = null ;

    //RSA key pair (public and private)
    private KeyPair rsaKey = null;

    //encrypted aes key and ivs combined
    private byte[] encryptedAESKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        wireUI();
        // specify spongyCastle for android runtime provider
    //    AESEncryptDecrypt.setProvider(new org.spongycastle.jce.provider.BouncyCastleProvider(), "SC");
      //  this.rsaKey = RSAEncryptDecrypt.generateRSAKey();
        //Toast.makeText(this, rsaKey.getPrivate().toString(), Toast.LENGTH_SHORT).show();
      //  Log.d("key is",rsaKey.toString());
    }

    private void wireUI()
    {
        this.inputtedUnencryptedText = (EditText)findViewById(R.id.inputtedUnencryptedText);
        this.encryptedText = (EditText)findViewById(R.id.encryptedText);
        this.decryptedText = (EditText)findViewById(R.id.decryptedText);

        this.encryptButton = (Button)findViewById(R.id.encryptButton);
        this.encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encryptButton();
            }
        });

        this.decryptButton = (Button)findViewById(R.id.decryptButton);
        this.decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decryptButton();
            }
        });

        this.clearButton = (Button)findViewById(R.id.clearButton);
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearButton();
            }
        });
    }

    private void decryptButton()
    {

        String encrypted = this.encryptedText.getText().toString();

       // String encrypted = "ANY_ENCRYPTED_STRING_HERE";
        String decrypted = "";
        try {
            decrypted = newclass.decrypt(encrypted,"");
            Log.d("TEST", "decrypted:" + decrypted);
            this.decryptedText.setText(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }



/*
        //sanity test on input from ui
        if (encText != null && encText.trim().length() > 0)
        {
            //decrypt the stored aes and ivs key
            byte[] decryptedAESKeyIVS = RSAEncryptDecrypt.decryptRSA(this.encryptedAESKey, this.rsaKey.getPrivate());
            //we combined the aes key and iv earlier in encryptButton() now after we decrypted
            //the value we split it up
            byte[] aesKey = Arrays.copyOfRange(decryptedAESKeyIVS, 0, 32);
            byte[] ivs = Arrays.copyOfRange(decryptedAESKeyIVS, 32, 48);

            char[] aesKeyChar = null;
            try
            {
                //convert the binary aes key to a char array
                aesKeyChar = new String(aesKey, "UTF-8").toCharArray();
            } catch (UnsupportedEncodingException e)
            {
                Log.e(EncryptionActivity.class.getName(), e.getMessage(), e);
                return;
            }

            //set up your streams for decryption
            ByteArrayInputStream encInputStream = new ByteArrayInputStream(Base64.decode(encText.toCharArray()));
            ByteArrayOutputStream plainTextOutputStream = new ByteArrayOutputStream(1024 * 10);
            String unencryptedString = "";
            Toast.makeText(this, String.copyValueOf(aesKeyChar), Toast.LENGTH_SHORT).show();
            Log.d("key is", String.copyValueOf(aesKeyChar));
            //main aes decrypt function
            AESEncryptDecrypt.aesDecrypt(encInputStream,
                    aesKeyChar,
                    ivs,
                    AESEncryptDecrypt.AESCipherType.AES_CBC_PKCS5PADDING,
                    plainTextOutputStream);

            try
            {
                //convert decrypted outputstream to a string
                unencryptedString = new String(plainTextOutputStream.toByteArray(),"UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                Log.e(EncryptionActivity.class.getName(), e.getMessage(), e);
                return;
            }

            //set decrypted text to the ui
            this.decryptedText.setText(unencryptedString);

        }


  */
    }

    private void clearButton()
    {
        this.inputtedUnencryptedText.setText(getString(R.string.default_hint));
        this.encryptedText.setText(" ");
        this.decryptedText.setText(" ");
        this.encryptedAESKey = null;
    }

    private void encryptButton()
    {

        String encrypted = "";
        String sourceStr = "This is any source string";
        try {
            encrypted = newclass.encrypt(sourceStr,"");
            Log.d("TEST", "encrypted:" + encrypted);
            encryptedText.setText(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }


       /* final String inputtedUnencryptedText = this.inputtedUnencryptedText.getText().toString();

        //sanity check on input
        if (TextUtils.isEmpty(inputtedUnencryptedText))
        {
            return;
        }

        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... params)
            {
                return encryptString(inputtedUnencryptedText);
            }

            @Override
            protected void onPostExecute(String encryptedString)
            {
                super.onPostExecute(encryptedString);
                if (encryptedString == null) return;
                encryptedText.setText(encryptedString);
                Log.d("string is",encryptedString);
                Log.e("string is",encryptedString);
            }
        }.execute();
*/
    }


}
