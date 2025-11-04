package Calculator;

import java.util.*;

/**
 * Калькулятор математических выражений с проверкой корректности
 */
public class MathExpressionCalculator {

    /**
     * Проверяет корректность математического выражения
     * @param expression математическое выражение
     * @return true если выражение корректно, false в противном случае
     */
    public boolean isValidExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        // Удаляем пробелы для упрощения проверки
        String expr = expression.replaceAll("\\s+", "");

        // Проверка баланса скобок
        if (!checkParenthesesBalance(expr)) {
            return false;
        }

        // Проверка на недопустимые символы
        if (!checkValidCharacters(expr)) {
            return false;
        }

        // Проверка синтаксиса операторов
        if (!checkOperatorsSyntax(expr)) {
            return false;
        }

        // Проверка чисел и точек
        if (!checkNumbersSyntax(expr)) {
            return false;
        }

        return true;
    }

    /**
     * Проверяет баланс скобок в выражении
     */
    private boolean checkParenthesesBalance(String expression) {
        int balance = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false; // Закрывающая скобка без открывающей
                }
            }
        }

        return balance == 0;
    }

    /**
     * Проверяет допустимость символов в выражении
     */
    private boolean checkValidCharacters(String expression) {
        // Допустимые символы: цифры, операторы, скобки, точка
        return expression.matches("^[0-9+\\-*/().]+$");
    }

    /**
     * Проверяет синтаксис операторов
     */
    private boolean checkOperatorsSyntax(String expression) {
        // Не может начинаться с */)
        if (expression.matches("^[*/)]")) {
            return false;
        }

        // Не может заканчиваться на оператор
        if (expression.matches(".*[+\\-*/]$")) {
            return false;
        }

        // Проверка на последовательные операторы (кроме +- которые могут быть унарными)
        for (int i = 0; i < expression.length() - 1; i++) {
            char current = expression.charAt(i);
            char next = expression.charAt(i + 1);

            // Запрещенные последовательности операторов
            if ("*/".indexOf(current) != -1 && "+-*/)".indexOf(next) != -1) {
                return false;
            }
            if (current == '+' && "+*/)".indexOf(next) != -1) {
                return false;
            }
            if (current == '-' && "+*/)".indexOf(next) != -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Проверяет корректность чисел
     */
    private boolean checkNumbersSyntax(String expression) {
        // Проверка на несколько точек в числе
        String[] parts = expression.split("[+\\-*/()]");
        for (String part : parts) {
            if (!part.isEmpty()) {
                long dotCount = part.chars().filter(ch -> ch == '.').count();
                if (dotCount > 1) {
                    return false;
                }
                // Точка не может быть в начале или конце числа
                if (part.startsWith(".") || part.endsWith(".")) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Вычисляет значение математического выражения
     * @param expression математическое выражение
     * @return результат вычисления
     * @throws IllegalArgumentException если выражение некорректно
     */
    public double evaluate(String expression) {
        if (!isValidExpression(expression)) {
            throw new IllegalArgumentException("Некорректное математическое выражение");
        }

        String expr = expression.replaceAll("\\s+", "");
        return evaluateExpression(expr);
    }

    /**
     * Рекурсивно вычисляет выражение
     */
    private double evaluateExpression(String expression) {
        // Обработка выражений в скобках
        while (expression.contains("(")) {
            expression = evaluateParentheses(expression);
        }

        // Вычисление умножения и деления
        expression = evaluateMultiplicationDivision(expression);

        // Вычисление сложения и вычитания
        return evaluateAdditionSubtraction(expression);
    }

    /**
     * Обрабатывает выражения в скобках
     */
    private String evaluateParentheses(String expression) {
        int openIndex = -1;
        int closeIndex = -1;
        int depth = 0;

        // Находим самые внутренние скобки
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                openIndex = i;
                depth = 1;
                for (int j = i + 1; j < expression.length(); j++) {
                    char c2 = expression.charAt(j);
                    if (c2 == '(') depth++;
                    if (c2 == ')') depth--;
                    if (depth == 0) {
                        closeIndex = j;
                        break;
                    }
                }
                break;
            }
        }

        if (openIndex != -1 && closeIndex != -1) {
            String inside = expression.substring(openIndex + 1, closeIndex);
            double result = evaluateExpression(inside);

            // Заменяем выражение в скобках на результат
            String before = expression.substring(0, openIndex);
            String after = expression.substring(closeIndex + 1);

            // Обрабатываем унарные плюс/минус перед скобками
            if (!before.isEmpty()) {
                char lastChar = before.charAt(before.length() - 1);
                if (lastChar == '+' || lastChar == '-') {
                    if (before.length() == 1 ||
                            "+-*/(".indexOf(before.charAt(before.length() - 2)) != -1) {
                        // Это унарный оператор
                        if (lastChar == '-') {
                            result = -result;
                        }
                        before = before.substring(0, before.length() - 1);
                    }
                }
            }

            return before + result + after;
        }

        return expression;
    }

    /**
     * Вычисляет умножение и деление
     */
    private String evaluateMultiplicationDivision(String expression) {
        // Разбиваем на токены, сохраняя операторы
        List<String> tokens = tokenize(expression);

        // Обрабатываем умножение и деление
        for (int i = 1; i < tokens.size() - 1; i += 2) {
            String operator = tokens.get(i);
            if (operator.equals("*") || operator.equals("/")) {
                double left = Double.parseDouble(tokens.get(i - 1));
                double right = Double.parseDouble(tokens.get(i + 1));
                double result = operator.equals("*") ? left * right : left / right;

                // Заменяем три токена на результат
                tokens.set(i - 1, String.valueOf(result));
                tokens.remove(i);
                tokens.remove(i);
                i -= 2; // Возвращаемся назад для обработки следующей операции
            }
        }

        // Собираем обратно в строку
        return String.join("", tokens);
    }

    /**
     * Вычисляет сложение и вычитание
     */
    private double evaluateAdditionSubtraction(String expression) {
        List<String> tokens = tokenize(expression);
        double result = Double.parseDouble(tokens.get(0));

        for (int i = 1; i < tokens.size(); i += 2) {
            String operator = tokens.get(i);
            double number = Double.parseDouble(tokens.get(i + 1));

            if (operator.equals("+")) {
                result += number;
            } else if (operator.equals("-")) {
                result -= number;
            }
        }

        return result;
    }

    /**
     * Разбивает выражение на токены
     */
    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '+' || c == '-' || c == '*' || c == '/') {
                // Если это оператор (но проверяем унарный минус/плюс)
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }

                // Проверка на унарный оператор
                if ((c == '+' || c == '-') &&
                        (tokens.isEmpty() ||
                                tokens.get(tokens.size() - 1).matches("[+\\-*/]"))) {
                    current.append(c);
                } else {
                    tokens.add(String.valueOf(c));
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    /**
     * Демонстрация работы калькулятора
     */
    public static void main(String[] args) {
        MathExpressionCalculator calculator = new MathExpressionCalculator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== КАЛЬКУЛЯТОР МАТЕМАТИЧЕСКИХ ВЫРАЖЕНИЙ ===");
        System.out.println("Поддерживаемые операции: +, -, *, /, скобки ()");
        System.out.println("Введите 'exit' для выхода");

        while (true) {
            System.out.print("\nВведите выражение: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            try {
                if (calculator.isValidExpression(input)) {
                    double result = calculator.evaluate(input);
                    System.out.println("Результат: " + result);
                } else {
                    System.out.println("ОШИБКА: Некорректное выражение");
                }
            } catch (Exception e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Программа завершена.");
    }
}
