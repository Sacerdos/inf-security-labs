package SRP6;

import javax.naming.InvalidNameException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
    private BigInteger N;   // безопасное простое
    private BigInteger g;   // генератор по модулю N
    private BigInteger k;   // параметр-множитель
    private BigInteger v;   // верификатор
    private BigInteger A;   // ОК клиента
    private BigInteger b;   // секретное значение сервера
    private BigInteger B;   // ОК сервера
    private BigInteger u;   // скремблер
    private BigInteger K;   // общий хэш-ключ сессии
    private String I;       // логин
    private String s;       // соль
    private Map<String, Pair<String, BigInteger>> BD = new HashMap<>(); //БД значений с клиента

    public Server(BigInteger N, BigInteger g, BigInteger k) {
        this.N = N;
        System.out.println("Сервер, безопасное простое: " + N);
        this.g = g;
        System.out.println("Сервер, генератор по модулю N: " + g);
        this.k = k;
        System.out.println("Сервер, параметр-множитель: " + k);
    }
    //Добавление при регистрации
    public void set_ISV(String I, String s, BigInteger v) throws InvalidNameException {
        if (!BD.containsKey(I)) {
            BD.put(I, new Pair<>(s, v));
        } else
            throw new InvalidNameException();
    }
    //Получаем от клиента его ОК:
    public void set_A(BigInteger A) throws IllegalAccessException {
        System.out.println("Сервер получил от Клиента, ОК клиента: " + A);
        // A != 0
        if (A.equals(BigInteger.ZERO))
            throw new IllegalAccessException();
        else
            this.A = A;
    }
    //Создание ОК сервера:
    public BigInteger create_B() {
        // b - случайное большое число
        b = new BigInteger(1024, new Random());
        System.out.println("Сервер, секретное число: " + b);
        // B = (k*v + g^b mod N) mod N
        B = (k.multiply(v).add(g.modPow(b, N))).mod(N);
        System.out.println("Сервер, ОК сервера: " + B);
        return B;
    }
    //генерация скремблера из А и В
    public void gen_u() throws IllegalAccessException {
        // u = H(A, B)
        u = SHA256.hash(A, B);
        System.out.println("Сервер, скремблер из А и В: " + u);
        // u != 0
        if (u.equals(BigInteger.ZERO))
            throw new IllegalAccessException();
    }
    //Сервер достаёт соль с верификатором для логина, вычисленные при регистрации
    public String get_s(String I) throws IllegalAccessException {
        if (BD.containsKey(I)) {
            this.I = I;
            s = BD.get(this.I).first;
            v = BD.get(this.I).second;
            System.out.println("Сервер, соль с верификатором для логина, вычисленные при регистрации: " + s);
            return s;
        } else
            throw new IllegalAccessException();
    }
    //Сервер со своей стороны так же вычисляет общий ключ сессии
    public void SessionKey() {
        // S = (A*(v^u mod N))^b mod N
        BigInteger S = A.multiply(v.modPow(u, N)).modPow(b, N);
        // K = H(S)
        K = SHA256.hash(S);
        System.out.println("Сервер, общий ключ сессии: " + K);
    }
    //Сервер у себя вычисляет M используя свою копию K, и проверяет равенство c M_C.
    public BigInteger create_M(BigInteger M_C) {
        // M = H(H(N) xor H(g), H(I), s, A, B, K)
        BigInteger M_S = SHA256.hash(SHA256.hash(N).xor(SHA256.hash(g)), SHA256.hash(I), s, A, B, K);
        System.out.println("Сервер, M сервера: " + K);
        System.out.println("Сервер получил от Клиента, M клиента: " + K);
        // R = H(A, M, K)
        if (M_S.equals(M_C))
            return SHA256.hash(A, M_S, K);
        else
            return BigInteger.ZERO;
    }
    //Заносим параметры - соль с верификатором
    private class Pair<A, B> {
        A first;
        B second;

        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}

