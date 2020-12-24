package SRP6;
import javax.naming.InvalidNameException;
import java.math.BigInteger;
import java.util.Scanner;
//На основе https://habr.com/ru/post/121021/
public class Main {
    public static void main(String[] args) {
        String N_hex = "0a:c0:37:c3:75:88:b4:32:98:87:e6:1c:2d:a3:32:" +
                "4b:1b:a4:b8:1a:63:f9:74:8f:ed:2d:8a:42:0c:2f:" +
                "c2:1b:12:32:f0:d3:bf:a0:24:27:6c:fd:88:44:81:" +
                "97:aa:e4:86:a6:3b:fc:a7:b8:bf:77:54:df:b3:27";
        BigInteger N = new BigInteger(N_hex.replace(":", ""), 16);
        System.out.println(N);
        BigInteger g = BigInteger.valueOf(2);
        // in SRP6a, k = H(N, g)
        BigInteger k = SHA256.hash(N, g);
        Server server = new Server(N, g, k);
        while (true) {
            System.out.println("Войти или зарегистрироваться?");
            System.out.println("1. Зарегистрироваться");
            System.out.println("2. Войти");
            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
            switch (choice) {
                // Регистрация
                case 1: {
                    System.out.println("Введите логин: ");
                    String login = input.next();

                    System.out.println("Введите пароль: ");
                    String password = input.next();
                    Client client = new Client(N, g, k, login, password);
                    client.set_SXV();
                    String s = client.get_s();
                    BigInteger v = client.get_v();
                    try {
                        server.set_ISV(login, s, v);
                    } catch (InvalidNameException e) {
                        System.out.println("Имя занято!");
                    }
                    break;
                }
                // Вход
                case 2: {
                    System.out.println("Введите логин: ");
                    String login = input.next();
                    System.out.println("Введите пароль: ");
                    String password = input.next();
                    Client client = new Client(N, g, k, login, password);
                    BigInteger A = client.gen_A();
                    try {
                        server.set_A(A);

                    } catch (IllegalAccessException e) {
                        System.out.println("A = 0");
                        break;
                    }
                    try {
                        String s = server.get_s(login);
                        BigInteger B = server.create_B();
                        client.receiveSaltAndB(s, B);
                    } catch (IllegalAccessException e) {
                        System.out.println("Такого пользователя не существует");
                        break;
                    }
                    try {
                        client.gen_u();
                        server.gen_u();
                    } catch (IllegalAccessException e) {
                        System.out.println("Соединение прервано!");
                        break;
                    }
                    client.SessionKey();
                    server.SessionKey();
                    BigInteger server_R = server.create_M(client.ClientConfirm());
                    if (client.compare_R_C(server_R))
                        System.out.println("Соединение установлено");
                    else
                        System.out.println("Неверный пароль");
                    break;
                }
                default:
                    return;
            }
            System.out.println();
        }
    }
}
