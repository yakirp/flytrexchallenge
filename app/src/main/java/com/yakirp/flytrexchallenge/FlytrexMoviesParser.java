package com.yakirp.flytrexchallenge;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.yakirp.flytrexchallenge.Utils.checkSum;
import static com.yakirp.flytrexchallenge.Utils.convertHexToString;
import static com.yakirp.flytrexchallenge.Utils.hex2Decimal;

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
            index++; //skip 01 byte

            //Copy the title from the payload to temp buffer
            System.arraycopy(payload, index, MaskedMovieTitle, 0, MaskedMovieTitle.length);

            //Move the index to point to next movie
            index = index - 3 + currentMovieSize;

            //Add the movie to the movies array
            FlytrexMovie movie = new FlytrexMovie();
            movie.setTitle(unMuskMovieName(MaskedMovieTitle));
            movie.setVerified(isVerified(MaskedMovieTitle, payload[index - 1]));
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
