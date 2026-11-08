import javax.swing.*;
import java.awt.*;

public class Calculator extends JFrame {
    private final JTextField display = new JTextField("0");
    private double firstNumber = 0;
    private String operator = "";
    private boolean startNewNumber = true;
    private boolean degrees = true;
    private JButton angleModeButton;

    public Calculator() {
        setTitle("Java Scientific Calculator");
        setSize(440, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        display.setFont(new Font("Arial", Font.PLAIN, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        JPanel scientificButtons = new JPanel(new GridLayout(4, 4, 6, 6));
        String[] scientificLabels = {
                "sin", "cos", "tan", "DEG",
                "log", "ln", "√", "|x|",
                "x^2", "x^y", "e^x", "π",
                "asin", "acos", "atan", "e", "e²"
        };
        addButtons(scientificButtons, scientificLabels);

        JPanel numberButtons = new JPanel(new GridLayout(5, 4, 6, 6));
        String[] numberLabels = {
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "−",
                "0", ".", "+/-", "+",
                "C", "DEL", "%", "="
        };
        addButtons(numberButtons, numberLabels);

        JPanel keypad = new JPanel(new BorderLayout(6, 6));
        keypad.add(scientificButtons, BorderLayout.NORTH);
        keypad.add(numberButtons, BorderLayout.CENTER);
        add(keypad, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addButtons(JPanel panel, String[] labels) {
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            if (label.equals("DEG")) {
                angleModeButton = button;
            }
            button.addActionListener(event -> handleButton(label));
            panel.add(button);
        }
    }

    private void handleButton(String input) {
        if (input.matches("\\d")) {
            if (startNewNumber || display.getText().equals("Error")) {
                display.setText(input);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + input);
            }
            return;
        }

        switch (input) {
            case "C" -> clear();
            case "DEL" -> deleteLastDigit();
            case "." -> addDecimalPoint();
            case "+/-" -> changeSign();
            case "%" -> applyUnaryOperation(value() / 100);
            case "pi" -> setValue(Math.PI);
            case "e" -> setValue(Math.E);
            case "DEG", "RAD" -> {
                degrees = !degrees;
                updateAngleModeButton();
            }
            case "=" -> {
                if (!operator.isEmpty()) {
                    if (calculatePendingOperation()) {
                        operator = "";
                        startNewNumber = true;
                    }
                }
            }
            case "+", "−", "×", "÷", "x^y" -> setOperator(input);
            default -> applyScientificOperation(input);
        }
    }

    private void setOperator(String nextOperator) {
        if (!operator.isEmpty() && !startNewNumber && !calculatePendingOperation()) {
            return;
        }

        firstNumber = value();
        operator = nextOperator;
        startNewNumber = true;
    }

    private boolean calculatePendingOperation() {
        double secondNumber = value();
        double result;

        switch (operator) {
            case "+" -> result = firstNumber + secondNumber;
            case "−" -> result = firstNumber - secondNumber;
            case "×" -> result = firstNumber * secondNumber;
            case "÷" -> {
                if (secondNumber == 0) {
                    showError();
                    return false;
                }
                result = firstNumber / secondNumber;
            }
            case "x^y" -> result = Math.pow(firstNumber, secondNumber);
            default -> {
                return false;
            }
        }

        return showResult(result);
    }

    private void applyScientificOperation(String operation) {
        double current = value();
        double angle = degrees ? Math.toRadians(current) : current;
        double result;

        switch (operation) {
            case "sin" -> result = Math.sin(angle);
            case "cos" -> result = Math.cos(angle);
            case "tan" -> result = Math.tan(angle);
            case "asin" -> {
                if (current < -1 || current > 1) {
                    showError();
                    return;
                }
                result = fromRadians(Math.asin(current));
            }
            case "acos" -> {
                if (current < -1 || current > 1) {
                    showError();
                    return;
                }
                result = fromRadians(Math.acos(current));
            }
            case "atan" -> result = fromRadians(Math.atan(current));
            case "log" -> {
                if (current <= 0) {
                    showError();
                    return;
                }
                result = Math.log10(current);
            }
            case "ln" -> {
                if (current <= 0) {
                    showError();
                    return;
                }
                result = Math.log(current);
            }
            case "sqrt" -> {
                if (current < 0) {
                    showError();
                    return;
                }
                result = Math.sqrt(current);
            }
            case "abs" -> result = Math.abs(current);
            case "x^2" -> result = current * current;
            case "e^x" -> result = Math.exp(current);
            default -> {
                return;
            }
        }

        showResult(result);
        startNewNumber = true;
    }

    private double fromRadians(double radians) {
        return degrees ? Math.toDegrees(radians) : radians;
    }

    private void addDecimalPoint() {
        if (startNewNumber || display.getText().equals("Error")) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void changeSign() {
        if (display.getText().equals("Error")) {
            return;
        }
        showResult(-value());
    }

    private void deleteLastDigit() {
        if (startNewNumber || display.getText().equals("Error")) {
            display.setText("0");
            startNewNumber = true;
            return;
        }

        String current = display.getText();
        current = current.substring(0, current.length() - 1);
        if (current.isEmpty() || current.equals("-")) {
            current = "0";
        }
        display.setText(current);
    }

    private double value() {
        if (display.getText().equals("Error")) {
            return 0;
        }
        return Double.parseDouble(display.getText());
    }

    private void setValue(double value) {
        showResult(value);
        startNewNumber = false;
    }

    private void applyUnaryOperation(double result) {
        showResult(result);
        startNewNumber = true;
    }

    private boolean showResult(double result) {
        if (!Double.isFinite(result)) {
            showError();
            return false;
        }
        display.setText(Double.toString(result));
        return true;
    }

    private void showError() {
        display.setText("Error");
        operator = "";
        startNewNumber = true;
    }

    private void clear() {
        display.setText("0");
        firstNumber = 0;
        operator = "";
        startNewNumber = true;
    }

    private void updateAngleModeButton() {
        angleModeButton.setText(degrees ? "DEG" : "RAD");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}
