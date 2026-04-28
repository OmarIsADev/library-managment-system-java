package com.system.librarymanagmentsystem;

import java.util.Scanner;

public class ConsoleUtils
{
    private static final Scanner scnr = new Scanner(System.in);

    public static boolean YesOrNoQuestion(String question)
    {
        System.out.println(question);
        String placeholder = scnr.next();

        if (placeholder.equalsIgnoreCase("yes") || placeholder.equalsIgnoreCase("y") || placeholder.equalsIgnoreCase("true"))
            return true;

        if (placeholder.equalsIgnoreCase("no") || placeholder.equalsIgnoreCase("n") || placeholder.equalsIgnoreCase("false"))
            return false;

        return YesOrNoQuestion("wrong answer please answer with yes or no");
    }

    public static int RangedIntegerQuestion(String question, int min, int max, String errorString)
    {
        int number;

        number = IntegerQuestion(question);

        while (number > max || number < min)
        {
            System.out.println(errorString);
            number = IntegerQuestion();
        }
        return number;
    }

    public static int RangedIntegerQuestion(String question, int min, int max)
    {
        return RangedIntegerQuestion(question, min, max, "the number you entered is invalid, please try a number between " + min + " and " + max);

    }

    public static int RangedIntegerQuestion(int min, int max)
    {
        return RangedIntegerQuestion("the number you entered is invalid, please try a number between " + min + " and " + max, min, max);
    }

    public static int IntegerQuestion(String question)
    {
        System.out.println(question);

        if (scnr.hasNextInt())
            return scnr.nextInt();

        scnr.next();
        return IntegerQuestion("invalid input, you need to enter an integer number");
    }

    public static int IntegerQuestion()
    {
        if (scnr.hasNextInt())
            return scnr.nextInt();

        scnr.next();
        return IntegerQuestion("invalid input, you need to enter an integer number");
    }

    public static double RangedDoubleQuestion(String question, double min, double max, String errorString)
    {
        double number;

        number = DoubleQuestion(question);

        while (number > max || number < min)
        {
            System.out.println(errorString);
            number = DoubleQuestion();
        }
        return number;
    }

    public static double RangedDoubleQuestion(String question, double min, double max)
    {
        return RangedDoubleQuestion(question, min, max, "the number you entered is invalid, please try a number between " + min + " and " + max);
    }

    public static double RangedDoubleQuestion(double min, double max)
    {
        return RangedDoubleQuestion("the number you entered is invalid, please try a number between " + min + " and " + max, min, max);
    }

    public static double DoubleQuestion(String question)
    {
        System.out.println(question);

        if (scnr.hasNextDouble())
            return scnr.nextDouble();

        scnr.next();
        return DoubleQuestion("invalid input, you need to enter a real number");

    }

    public static double DoubleQuestion()
    {
        if (scnr.hasNextDouble())
            return scnr.nextDouble();

        scnr.next();
        return DoubleQuestion("invalid input, you need to enter a real number");
    }
}
