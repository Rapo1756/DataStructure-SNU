import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class BigInteger
{
    final byte[] item;
    final int length;
    final boolean isNegative;
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "Wrong Input";
    // implement this
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile(" *[+-]? *(0|[1-9]\\d*) *[+\\-*] *[+-]? *(0|[1-9]\\d*) *");

    public static int numDigit(int i) {
        int digit = 0;
        while(i != 0){
            i /= 10;
            digit++;
        }
        return digit;
    }

    public BigInteger(int i)
    {
        isNegative = i < 0;
        length = numDigit(i);
        item = new byte[length];
        for(int j = 0; j < length; j++){
            item[j] = (byte) (i % 10);
            i /= 10;
        }
    }
  
    public BigInteger(byte[] num1, boolean isNegative)
    {
        length = num1.length;
        this.isNegative = (length != 1 || num1[0] != 0) && isNegative;
        item = new byte[length];
        for(int j = 0; j < num1.length; j++){
            item[j] = num1[j];
        }
    }
  
    public BigInteger(String s)
    {
        char sgn_c = s.charAt(0);
        if(sgn_c == '-' || sgn_c == '+'){
            length = s.length() - 1;
            isNegative = (length != 1 || s.charAt(1) != '0') && sgn_c == '-';
            item = new byte[length];
            for(int j = 0; j < length; j++){
                item[j] = (byte) (s.charAt(length - j)-'0');
            }
        }
        else {
            isNegative = false;
            length = s.length();
            item = new byte[length];
            for(int j = 0; j < length; j++){
                item[j] = (byte) (s.charAt(length - j - 1)-'0');
            }
        }
    }

    public static byte[] extend(byte[] num, byte ext){
        int numLength = num.length;
        byte[] result = new byte[numLength + 1];
        result[numLength] = ext;
        System.arraycopy(num, 0, result, 0, numLength);
        return result;
    }

    public static byte[] stripZero(byte[] num){
        int numLength = num.length;
        if(num[numLength-1]!=0) return num;
        int zeroLength = 0;
        for(int j = numLength - 1; num[j] == 0 && j > 0; j--){
            zeroLength++;
        }
        int resultLength = numLength - zeroLength;
        byte[] result = new byte[resultLength];
        System.arraycopy(num, 0, result, 0, resultLength);
        return result;
    }

    public static byte[] add(byte[] num1, byte[] num2){
        if(num1.length < num2.length){
            byte[] temp = num2;
            num2 = num1;
            num1 = temp;
        }
        int num1Digit = num1.length;
        int num2Digit = num2.length;
        byte[] result = new byte[num1Digit];
        int carry = 0;
        for(int i = 0; i < num1Digit; i++){
            if(i < num2Digit) {
                int sum = num1[i] + num2[i] + carry;
                carry = sum >= 10 ? 1 : 0;
                result[i] = (byte) (sum % 10);
            }
            else{
                result[i] = (byte) (num1[i] + carry);
                carry = 0;
            }
        }
        if(carry != 0){
            return extend(result, (byte)1);
        }
        return result;
    }

    public static byte[] subtract(byte[] num1, byte[] num2){
        int num1Digit = num1.length;
        int num2Digit = num2.length;
        byte[] result = new byte[num1Digit];
        for(int i = 0; i < num1Digit; i++){
            if(i < num2Digit) {
                int diff = num1[i] - num2[i];
                if (diff < 0) {
                    num1[i + 1]--;
                    diff += 10;
                }
                result[i] = (byte) diff;
            }
            else{
                result[i] = num1[i];
            }
        }
        return stripZero(result);
    }

    public static byte[] multiply(byte[] num1, byte[] num2){
        if(Arrays.equals(num1, new byte[]{0})||Arrays.equals(num2, new byte[]{0})) return new byte[]{0};
        int num1Digit = num1.length;
        int num2Digit = num2.length;
        int resultDigit = num1Digit + num2Digit - 1;
        byte[] result = new byte[resultDigit];
        int carry = 0;
        for(int i = 0; i < num1Digit; i++){
            carry = 0;
            for(int j = 0; j < num2Digit; j++) {
                int cal = result[i+j] + num1[i] * num2[j] + carry;
                result[i+j] = (byte) (cal % 10);
                carry = cal / 10;
            }
            if(i!=num1Digit-1) result[i+num2Digit] = (byte) carry;
        }
        if(carry != 0){
            return extend(result, (byte) carry);
        }
        return stripZero(result);
    }

    public static boolean needChange(byte[] num1, byte[] num2){
        if (num1.length == num2.length) {
            for(int i = num1.length - 1; i >= 0; i--){
                if(num1[i] == num2[i]) continue;
                return num1[i] < num2[i];
            }
            return false;
        }
        else return num1.length < num2.length;
    }

    public BigInteger add(BigInteger big)
    {
        if(this.isNegative == big.isNegative){
            return new BigInteger(add(this.item, big.item), this.isNegative);
        }
        else if(needChange(this.item, big.item))
            return new BigInteger(subtract(big.item, this.item), big.isNegative);
        else
            return new BigInteger(subtract(this.item, big.item), this.isNegative);

    }
  
    public BigInteger subtract(BigInteger big)
    {
        if(this.isNegative != big.isNegative){
            return new BigInteger(add(this.item, big.item), this.isNegative);
        }
        else if(needChange(this.item, big.item))
            return new BigInteger(subtract(big.item, this.item), !this.isNegative);
        else
            return new BigInteger(subtract(this.item, big.item), this.isNegative);
    }
  
    public BigInteger multiply(BigInteger big)
    {
        if(needChange(this.item, big.item))
            return new BigInteger(multiply(big.item, this.item), this.isNegative != big.isNegative);
        else
            return new BigInteger(multiply(this.item, big.item), this.isNegative != big.isNegative);
    }
  
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        if(isNegative) s.append('-');
        for(int j = item.length - 1; j >= 0; j--){
            s.append(item[j]);
        }
        return s.toString();
    }
  
    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
        if(!EXPRESSION_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException();
        }
        String[] inputSplit = input.replaceAll(" +", "").split("");
        StringBuilder arg1 = new StringBuilder();
        StringBuilder arg2 = new StringBuilder();
        int idx;
        for(idx = 0; !(inputSplit[idx].matches("[+\\-*]")&&idx>0);idx++){
            arg1.append(inputSplit[idx]);
        }
        String op = inputSplit[idx];
        for(++idx; idx < inputSplit.length ;idx++){
            arg2.append(inputSplit[idx]);
        }
        BigInteger num1 = new BigInteger(arg1.toString());
        BigInteger num2 = new BigInteger(arg2.toString());
        if(op.equals("+")) return num1.add(num2);
        else if(op.equals("-")) return num1.subtract(num2);
        else if(op.equals("*")) return num1.multiply(num2);
        else return null;
    }
  
    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
  
                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }
  
    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);
  
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
  
            return false;
        }
    }
  
    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
