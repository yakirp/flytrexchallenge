package com.yakirp.flytrexchallenge;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.yakirp.flytrexchallenge.Utils.checkSum;
import static com.yakirp.flytrexchallenge.Utils.convertHexToString;
import static com.yakirp.flytrexchallenge.Utils.hex2Decimal;
import static com.yakirp.flytrexchallenge.Utils.hexStringToByteArray;

/**
 * Created by yakirp on 8/30/2016.
 */
public class FlytrexMoviesParser {
    private List<FlytrexMovie> movies;

    /***
     * Parse the movies payload to FlytrexMovie List
     *
     * @param payload to parse. from given URL
     * @return FlytrexMovie List
     */
    public List<FlytrexMovie> parse(byte[] payload) {
        movies = new ArrayList<>();
        //If the movies payload is null or empty we return empty movies list
        if (payload == null || payload.length == 0) {
            return movies;
        }

        int index = 0;
        while (index < payload.length) {
            index++; //skip 3f
            int currentMovieSize = payload[index]; //Save the current movie payload size

            byte[] MaskedMovieTitle = new byte[payload[index] - 4];
            index++; //skip size byte
            byte xorKey = payload[index];

            index++; //skip 01 byte

            //Copy the title from the payload to temp buffer
            System.arraycopy(payload, index, MaskedMovieTitle, 0, MaskedMovieTitle.length);

            //Move the index to point to next movie
            index = index - 3 + currentMovieSize;

            //Add the movie to the movies array
            FlytrexMovie movie = new FlytrexMovie();
            movie.setTitle(unMuskMovieName1(MaskedMovieTitle,Integer.toHexString(xorKey & 0xFF)));
            movie.setVerified(isVerified1(MaskedMovieTitle, payload[index - 1],xorKey));
            movies.add(movie);
        }
        return movies;
    }

    /***
     * Check if the moview title checksum is OK
     *
     * @param maskedName to check
     * @param sign       the current sign to verify
     * @return true is verify
     */
    private boolean isVerified(byte[] maskedName, byte sign) {

        if (maskedName == null || maskedName.length == 0) {
            return false;
        }


        int signValue = Integer.valueOf(hex2Decimal(Integer.toHexString(checkSum(maskedName))));
        if ((signValue % 2) == 1) {
            signValue--;
        } else {
            signValue++;
        }
        return sign == signValue;
    }

    private boolean isVerified1(byte[] maskedName, byte sign, byte xorKey) {
        return Integer.toHexString((int) new BigInteger(Integer.toHexString(xorKey & 0xFF), 16).intValue() ^ (int) checkSum(maskedName) & 0xFF).equals(Integer.toHexString(sign));
   }


    private String unMuskMovieName1(byte[] maskedName ,String key) {

        byte[] output = new byte[maskedName.length];

        int i = 0;
        for(byte b : maskedName){

            int one = (int)b;
            int two = (int)hexStringToByteArray(key)[0];
            int xor = one ^ two;
            output[i++] = (byte)(0xff & xor);

        }



        return new String(output);

    }

    private String unMuskMovieName(byte[] maskedName) {
        StringBuffer hex = new StringBuffer();
        for (byte b : maskedName) {
            BigInteger bigInt = new BigInteger(Integer.toHexString(b), 16);
            if ((Integer.valueOf(hex2Decimal(Integer.toHexString(b))) % 2) == 1) {
                bigInt = bigInt.subtract(BigInteger.ONE);
            } else {
                bigInt = bigInt.add(BigInteger.ONE);
            }
            hex.append(bigInt.toString(16));
        }
        return convertHexToString(hex.toString());
    }


}
