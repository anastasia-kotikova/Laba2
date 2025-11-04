import Calculator.MathExpressionCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для калькулятора математических выражений
 */
class MathExpressionCalculatorTest {
    private MathExpressionCalculator calculator = new MathExpressionCalculator();

    @Test
    void testValidExpressions() {
        assertTrue(calculator.isValidExpression("2 + 2"));
        assertTrue(calculator.isValidExpression("(2 + 3) * 4"));
        assertTrue(calculator.isValidExpression("10.5 - 2.5"));
        assertTrue(calculator.isValidExpression("3 * (4 + 5) / 2"));
        assertTrue(calculator.isValidExpression("1 + 2 * 3 - 4 / 2"));
    }

    @Test
    void testInvalidExpressions() {
        assertFalse(calculator.isValidExpression("2 + ")); // Заканчивается на оператор
        assertFalse(calculator.isValidExpression("* 2 + 3")); // Начинается с *
        assertFalse(calculator.isValidExpression("2 + 3)")); // Несбалансированные скобки
        assertFalse(calculator.isValidExpression("(2 + 3")); // Несбалансированные скобки
        assertFalse(calculator.isValidExpression("2 + 3a")); // Недопустимый символ
        assertFalse(calculator.isValidExpression("2..5 + 3")); // Две точки в числе
        assertFalse(calculator.isValidExpression("2 * / 3")); // Два оператора подряд
    }

    @Test
    void testEvaluation() {
        assertEquals(4.0, calculator.evaluate("2 + 2"));
        assertEquals(20.0, calculator.evaluate("(2 + 3) * 4"));
        assertEquals(8.0, calculator.evaluate("10.5 - 2.5"));
        assertEquals(13.5, calculator.evaluate("3 * (4 + 5) / 2"));
        assertEquals(5.0, calculator.evaluate("1 + 2 * 3 - 4 / 2"));
        assertEquals(1.0, calculator.evaluate("(1 + 2) * 3 / (4 + 5)"));
    }

    @Test
    void testComplexExpressions() {
        assertEquals(14.0, calculator.evaluate("2 + 3 * 4"));
        assertEquals(20.0, calculator.evaluate("(2 + 3) * 4"));
        assertEquals(2.5, calculator.evaluate("10 / 4"));
        assertEquals(8.0, calculator.evaluate("2 * (3 + 1)"));
    }
}