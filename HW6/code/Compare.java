import java.io.*;

public class Compare {
    public static void main(String[] args) {
        if (args.length == 0) {
            for (int i = 1; i <= 60; i++) {
                String my_output = "my_output/" + i + ".txt";
                String answer = "testset/output/" + i + ".txt";
                command(my_output, answer);
            }
            return;
        } else if (args.length == 2) {
            command(args[0], args[1]);
            return;
        }
        System.out.println("Usage: java Compare [my_output] [answer] or java Compare");
        System.exit(1);
    }

    private static void command(String my_output, String answer) {
        try (BufferedReader br1 = new BufferedReader(new FileReader(my_output));
                BufferedReader br2 = new BufferedReader(new FileReader(answer))) {
            String line1, line2;
            boolean wrongRes = false;
            boolean pathDiff = false;
            do {
                line1 = br1.readLine();
                line2 = br2.readLine();
                if (line1 == null || line2 == null)
                    continue;
                line1 = line1.trim();
                line2 = line2.trim();
                if (!line1.equals(line2)) {
                    System.out.println("< " + line1);
                    System.out.println("> " + line2);
                    if (Integer.getInteger(line1) == null && Integer.getInteger(line2) == null)
                        pathDiff = true;
                    else
                        wrongRes = true;
                }
            } while (line1 != null && line2 != null);
            if (line1 != null || line2 != null) {
                wrongRes = true;
                if (line1 != null)
                    do {
                        System.out.println("< " + line1);
                        System.out.println("> ");
                        line1 = br1.readLine();
                    } while (line1 != null);
                if (line2 != null)
                    do {
                        System.out.println("< ");
                        System.out.println("> " + line2);
                        line2 = br1.readLine();
                    } while (line2 != null);

            }
            if (wrongRes)
                System.out.println(my_output + " has wrong result.");
            else if (pathDiff)
                System.out.println(my_output + " has different path with " + answer + ".");
        } catch (IOException e) {
            System.out.println("입력이 잘못되었습니다. 오류 : " + e);
        }
    }
}
