import java.math.BigDecimal;
import java.math.BigInteger;

public class RSA {
    static class MethodRSA{
        int n=0;
        int z=0;
        int e=0;
        int d=0;
        double x=0;
        public MethodRSA(int p, int q){
            n = p*q;
            z = (p-1)*(q-1);
            System.out.println("Функция Эйлера = " + z);
            for(int i=2;i<z;i++){
                if(GCD(i, z)==1){
                    e=i;
                    System.out.println("Открытая экспонента = " + e);
                    break;
                }
            }
            for (int i = 0; i < z; i++) {
                x=1+(i*z);
                if(x%e==0){
                    d=(int)(x/e);
                    break;
                }
            }
            System.out.println("Секретная экспонента = " + d);
        }
        public int GCD(int a, int b) {
            if (b==0) return a;
            return GCD(b,a%b);
        }
        public double encrypt(String message){
            return (Math.pow(Integer.parseInt(message),e))%n;
        }
        public BigInteger decrypt(double message){
            BigInteger N = BigInteger.valueOf(n);
            BigInteger C = BigDecimal.valueOf(message).toBigInteger();
            return (C.pow(d)).mod(N);
        }
    }


    public static void main(String[] args) {
        MethodRSA rsa = new MethodRSA(3557,2579);
        System.out.println("Public key = [" + rsa.e + ", " + rsa.n +"]");
        System.out.println("Private key = [" + rsa.d + ", " + rsa.n +"]");
        String original = "111111";
        System.out.println("Original message: " + original);
        double encryptMessage = rsa.encrypt(original);
        System.out.println("Encrypted message: " + (int)encryptMessage);
        System.out.println("Decrypted message: " + rsa.decrypt(encryptMessage));
    }
}
