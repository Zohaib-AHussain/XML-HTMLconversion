package com.zh.xml_htmlconversion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import full.org.apache.xalan.processor.TransformerFactoryImpl;
import full.org.apache.xalan.transformer.TransformerImpl;
import full.org.apache.xml.serializer.utils.SystemIDResolver;


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

            /*
            *
            * The old solution using built in parser
            *
            * try {

                    Source xmlSource = new StreamSource(context.getResources().openRawResource(R.raw.sample1));
                    Source xsltSource = new StreamSource(context.getResources().openRawResource(R.raw.spl));


                    TransformerFactory transFact = TransformerFactory.newInstance();
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
                    Transformer trans = transFact.newTransformer(xsltSource);

                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mydata.html");

                    StreamResult result = new StreamResult(f);
                    trans.transform(xmlSource, result);

                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerFactoryConfigurationError e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
               }
            *
            *
            * */


            /*
            *
            * new solution, generated file is worst (empty) previously had some html data
            * */
            Source xmlSource = new StreamSource(new BufferedInputStream(getAssets().open("sample1.xml")));
            Source xsltSource = new StreamSource(new BufferedInputStream(getAssets().open("spl.xsl")));

            xmlSource.setSystemId(xmlSource.getSystemId());
            xsltSource.setSystemId(xsltSource.getSystemId());

            /*
            * I have added the Xalan-2.7.1 and Serializer-2.7.1 after using jarjar. The xslt file (spl-common.xsl)
            * requires additional extension which are not avaliable in android hence I had to add these extra jars.
            *
            * This is what I have come up with not sure if any of this is correct
            *
            * */

            TransformerFactoryImpl transFact = new TransformerFactoryImpl();
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

            TransformerImpl trans = (TransformerImpl) transFact.newTransformer(xsltSource);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydata.html");

            StreamResult result = new StreamResult(f);
            trans.transform(xmlSource, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
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
        } else {
            xmlToHtml();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        xmlToHtml();
    }
}
