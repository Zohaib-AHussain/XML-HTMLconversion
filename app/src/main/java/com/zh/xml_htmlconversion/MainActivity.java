package com.zh.xml_htmlconversion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForExternalStoragePermission();

        xmlToHtml();
    }

    private void xmlToHtml() {
        try {

            //new BufferedInputStream(context.getAssets().open("spl.xsl"));
            Source xmlSource = new StreamSource(new BufferedInputStream(getAssets().open("sample1.xml")));
            Source xsltSource = new StreamSource(new BufferedInputStream(getAssets().open("spl.xsl")));

/*            xmlSource.setSystemId(Environment.getExternalStorageDirectory().getAbsolutePath());
            xsltSource.setSystemId(Environment.getExternalStorageDirectory().getAbsolutePath());*/
            TransformerFactory transFact = TransformerFactory.newInstance();
            //transFact.setAttribute ( "generate-translate" , Boolean.TRUE ) ;
            transFact.setURIResolver(new URIResolver() {
                @Override
                public Source resolve(String href, String base) throws TransformerException {
                    try {
                        return new StreamSource(new BufferedInputStream(getAssets().open(href)));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });


/*            // transformer created by factory.newTransformer(...)
            TransformerImpl xalanTransformer = (TransformerImpl)transformer;
            StylesheetRoot sroot = new StylesheetRoot((ErrorListener)null);
// creates an empty Vector to be returned from getExtensions()
            sroot.getExtensionNamespacesManager();
// make sure the workaround works in Marsmallow too
            sroot.setSecureProcessing(false);
// sets up an empty ExtensionTable, so functionAvailable works without NPE
            xalanTransformer.setExtensionsTable(sroot);*/



            Transformer trans = transFact.newTransformer(xsltSource);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mydata.html");

            StreamResult result = new StreamResult(f);
            trans.transform(xmlSource, result);

        } catch (TransformerConfigurationException e) {
            Log.d(TAG, "TransformerConfigurationException: ");
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            Log.d(TAG, "TransformerFactoryConfigurationError: ");
            e.printStackTrace();
        } catch (TransformerException e) {
            Log.d(TAG, "TransformerException: "+ e);
            e.printStackTrace();
        } catch (IOException e){
            Log.d(TAG, "IOException: ");
        }
    }

    private void askForExternalStoragePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                xmlToHtml();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }else{
            xmlToHtml();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        xmlToHtml();
    }
}
