package RoundRobinCalculator;

import java.util.Arrays;

/**
 * The type Pairing builder.
 */
public class PairingBuilder {


    /**
     * Build pairing normal string.
     *
     * @param white the white
     * @param black the black
     * @return the string
     */
    public String buildPairingNormal(String white, String black) {
        return "\n" + white + " **(White)** **vs** " + black + " **(Black)** \n\n";
    }

    /**
     * Build pairing for automated url string.
     *
     * @param white the white
     * @param black the black
     * @param URL   the url
     * @return the string
     */
    public String buildPairingForAutomatedURL(String white, String black, String URL) {
        return white + " **(White)** **vs** " + black + " **(Black)** " + URL + " \n";
    }

    /**
     * Build pairing transformer string [ ].
     *
     * @param pairing the pairing
     * @return the string [ ]
     */
    public String[] buildPairingTransformer(String pairing) {

        String[] res = new String[4];
        String[] spliter = pairing.split(" ");

        res[0] = spliter[0].replace("\n", "");
        res[1] = spliter[3].replace("\n", "");
        res[2] = spliter[1];
        res[3] = spliter[4];

        System.out.println(Arrays.toString(res));
        return res;
    }


}
