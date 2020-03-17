package com.ecommerce.ecommercecart.API;

import com.ecommerce.ecommercecart.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.omg.PortableServer.POA;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertTrue;


@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, HttpURLConnection.class, ProductAPI.class, BufferedReader.class, InputStreamReader.class})
class ProductAPITest {

    @InjectMocks
    private ProductAPI productAPI;

    @Test
    public void getProductByIDTest() throws Exception {

        class UrlWrapper {

            URL url;

            public UrlWrapper(String spec) throws MalformedURLException {
                url = new URL(spec);
            }

            public URLConnection openConnection() throws IOException {
                return url.openConnection();
            }
        }

        UrlWrapper url = PowerMockito.mock(UrlWrapper.class);
//        HttpURLConnection httpURLConnection = PowerMockito.mock(HttpURLConnection.class);

        InputStreamReader inputStreamReader = PowerMockito.mock(InputStreamReader.class);
        BufferedReader bufferedReader = PowerMockito.mock(BufferedReader.class);
        PowerMockito.whenNew(InputStreamReader.class).withAnyArguments().thenReturn(inputStreamReader);
        PowerMockito.whenNew(BufferedReader.class).withAnyArguments().thenReturn(bufferedReader);

        StringBuilder stringBuilder = PowerMockito.mock(StringBuilder.class);
        PowerMockito.whenNew(StringBuilder.class).withNoArguments().thenReturn(stringBuilder);

        productAPI = PowerMockito.spy(new ProductAPI());

        PowerMockito.doReturn(new Product()).when(productAPI).getProductByID(1);

        ObjectMapper objectMapper = PowerMockito.mock(ObjectMapper.class);
        PowerMockito.whenNew(ObjectMapper.class).withAnyArguments().thenReturn(objectMapper);

        PowerMockito.when(objectMapper.readValue("string", Product.class)).thenReturn(new Product());


        Mockito.verify(bufferedReader, Mockito.times(1)).readLine();
        Mockito.verify(objectMapper, Mockito.times(1)).readValue("string",Product.class);
        Mockito.verify(url,Mockito.times(1)).openConnection();



    }
}