package com.zh.xml_htmlconversion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import full.org.apache.xalan.processor.TransformerFactoryImpl;
import full.org.apache.xalan.transformer.TransformerImpl;


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
            Log.e("HTML", "XMLTOHTML METHOD RUNNING>>>>>>>>>>>>>>>>>>>>>>>>");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setURIResolver(new URIResolver() {
                @Override
                public Source resolve(String href, String base) throws TransformerException {
                    try {
                        Log.e("HTML", "href: " + href);
                        return new StreamSource(new BufferedInputStream(getAssets().open(href)));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
			Source xslDoc = new StreamSource(new BufferedInputStream(getAssets().open("sample1.xml")));
			Source xmlDoc = new StreamSource(new BufferedInputStream(getAssets().open("spl.xsl")));

            Transformer trasform = tFactory.newTransformer(xslDoc);
            OutputStream htmlFile = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydata.html"));
			trasform.transform(xmlDoc, new StreamResult(htmlFile));
            Log.e("HTML", "Done with parsing>>>>>>>>>>>>>>>>>>>>>>>>");

            ////////////////////////////////////////////////////////////////////////////////////////////
//            Log.e("HTML", "XMLTOHTML METHOD RUNNING");
//            Source xmlSource = new StreamSource(new BufferedInputStream(getAssets().open("sample1.xml")));
//            Source xsltSource = new StreamSource(new BufferedInputStream(getAssets().open("spl.xsl")));
//
//            Log.e("XML", xmlSource.toString());
//            Log.e("XML", xsltSource.toString());
//
//            TransformerFactoryImpl transFact = new TransformerFactoryImpl();
//            transFact.setURIResolver(new URIResolver() {
//                @Override
//                public Source resolve(String href, String base) throws TransformerException {
//                    try {
//                        Log.e("HTML", "href: " + href);
//                        return new StreamSource(new BufferedInputStream(getAssets().open(href)));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//            });
//
//            TransformerImpl trans = (TransformerImpl) transFact.newTransformer(xsltSource);
//            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydata.html");
//
//            Log.e("HTML", "File path: " + f.getAbsolutePath());
//            StreamResult result = new StreamResult(f);
//            trans.transform(xmlSource, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            Log.e("HTML", e.getLocalizedMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            Log.e("HTML", e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("HTML", e.getLocalizedMessage());
        }
        Log.e("HTML", "Done with parsing");
    }

    private void askForExternalStoragePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
//                xmlToHtml();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else {
//            xmlToHtml();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        xmlToHtml();
    }
}
