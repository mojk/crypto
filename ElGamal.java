import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.math.BigDecimal;


public class ElGamal {

  public static String decodeMessage(BigInteger m) {
    return new String(m.toByteArray());
  }  

  public static void main(String[] arg) {
    String filename = "input_elgamal.txt";
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      BigInteger p = new BigInteger(br.readLine().split("=")[1]); // GROUPSIZE
      BigInteger g = new BigInteger(br.readLine().split("=")[1]); // GENERATOR
      BigInteger y = new BigInteger(br.readLine().split("=")[1]); // PUBLIC KEY OF THE RECIEVER
      String line = br.readLine().split("=")[1];
      String date = line.split(" ")[0];
      String time = line.split(" ")[1];
      int year  = Integer.parseInt(date.split("-")[0]);
      int month = Integer.parseInt(date.split("-")[1]);
      int day   = Integer.parseInt(date.split("-")[2]);
      int hour   = Integer.parseInt(time.split(":")[0]);
      int minute = Integer.parseInt(time.split(":")[1]);
      int second = Integer.parseInt(time.split(":")[2]);
      BigInteger c1 = new BigInteger(br.readLine().split("=")[1]); // ELGAMAL ENCRYPTION
      BigInteger c2 = new BigInteger(br.readLine().split("=")[1]); // ELGAMAL ENCRYPTION
      br.close();
      BigInteger m = recoverSecret(p, g, y, year, month, day, hour, minute,
          second, c1, c2);
      System.out.println("Recovered message: " + m);
      System.out.println("Decoded text: " + decodeMessage(m));
    } catch (Exception err) {
      System.err.println("Error handling file.");
      err.printStackTrace();
      System.exit(1);
    }
  }
  
  public static BigInteger recoverSecret(BigInteger p, BigInteger g,
      BigInteger y, int year, int month, int day, int hour, int minute,
      int second, BigInteger c1, BigInteger c2) {
//-----------------------------------------------------------------------------------------------------------------
// DECRYPTING
// Key = c1 ^ x, where x => generator ^ x mod p (group) = public key of reciever (y)
// message = c2 * Modinverse(key)
//-----------------------------------------------------------------------------------------------------------------
    BigInteger randomnumber = BigInteger.valueOf((int) (year * Math.pow(10,10)) ).add(BigInteger.valueOf((int) (month * Math.pow(10,8)) )).add( 
                              BigInteger.valueOf((int) (day * Math.pow(10,6))) ).add(BigInteger.valueOf((int) (hour * Math.pow(10,2)) )).add( 
                              BigInteger.valueOf((int) (second))).add(BigInteger.valueOf((int) (second * Math.pow(10,-3))));
    BigInteger test = g.modPow(randomnumber, p); //does not work
    System.out.println("test" + test);
    System.out.println("c1" + c1);
    return test;
  }
  
}