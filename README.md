# Movie Ticket Booking System (S3 Java OOP Project)

Architecture: **Java Swing -> Java OOP classes -> JDBC -> MySQL**

## Folder structure
```
MovieBookingSystem/
├── sql/movie_booking_db.sql
├── lib/                      <- put mysql-connector-j-x.x.x.jar here
└── src/
    ├── exception/  BookingException
    ├── model/      Movie, Show, Customer, Booking
    ├── payment/    Payable (interface), Payment (abstract), CashPayment, CardPayment
    ├── database/   DatabaseConnection
    ├── service/    MovieBookingSystem
    └── gui/        MainMenuFrame, MovieSelectionFrame, BookingFrame,
                    ConfirmationFrame, BookingHistoryFrame
```

## 1. Create the database
1. Install MySQL and start it.
2. Run the script:
   - Command line: `mysql -u root -p < sql/movie_booking_db.sql`
   - or open the file in MySQL Workbench and click Execute.
3. It creates `movie_booking_db`, 4 tables, and the sample data.

## 2. Configure JDBC
1. The MySQL Connector JAR (`mysql-connector-j-8.4.0.jar`) is already placed in `lib/` for convenience.
2. Open `src/database/DatabaseConnection.java` and change `USER` and `PASSWORD` if your MySQL uses different credentials (port 3306 by default).

## 3. Compile and run (JDK 11+)
Linux / Mac:
```
mkdir out
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" gui.MainMenuFrame
```
Windows (Command Prompt):
```
mkdir out
dir /s /b src\*.java > sources.txt
javac -cp "lib/*" -d out @sources.txt
java -cp "out;lib/*" gui.MainMenuFrame
```
IntelliJ: open the folder, mark `src` as Sources Root, add the JAR (File > Project Structure > Libraries), run `MainMenuFrame`.

## 4. Where each syllabus concept is used
**Class and Object:** `Movie`, `Show`, `Customer`, `Booking` are classes. Objects are created like `new Movie(...)` in `MovieBookingSystem.getMovies()` and `new CashPayment()` in `BookingFrame.getSelectedPayment()`.

**Encapsulation:** All model fields are `private` (e.g. `Movie.title`) and accessed through getters/setters (`getTitle()`, `setTitle()`).

**Abstraction:** `Payment` is an abstract class with the abstract method `getFinalAmount()`. You cannot write `new Payment()`.

**Inheritance:** `CashPayment extends Payment` and `CardPayment extends Payment`. `BookingException extends Exception`. The GUI frames `extend JFrame`.

**Polymorphism:** `BookingFrame.getSelectedPayment()` returns a `Payment` that is really Cash or Card. `MovieBookingSystem.bookTicket(...)` calls `payment.getFinalAmount(...)` and gets a different result depending on the real object (Card adds 2%).

**Constructor:** `Movie()` and `Movie(int, String, ...)` (overloaded constructors); `Booking(...)`; `CashPayment()` calls `super("Cash")`.

**Method Overloading:** `getBookingHistory()` (all bookings) and `getBookingHistory(String customerName)` (search), both in `MovieBookingSystem`, used in `BookingHistoryFrame.loadTable()`.

**Method Overriding:** `getFinalAmount()` and `pay()` overridden in `CashPayment` and `CardPayment`; `toString()` overridden in `Movie` and `Show`; `actionPerformed()` overridden in every frame.

**Interface:** `Payable` (method `pay`) is implemented by `Payment`. `ActionListener` is implemented by every frame.

**Access modifiers:** `private` fields in models, `public` methods, `protected String methodName` in `Payment`, `private` helper methods like `getOrCreateCustomer()`.

**this keyword:** constructors/setters, e.g. `this.title = title;` in `Movie`; `this.system = system;` in frames.

**super keyword:** `super("Cash")` in `CashPayment`; `super(message)` in `BookingException`; `super("Book Tickets")` in `BookingFrame`.

**Exception Handling:** `try/catch/finally` in every `MovieBookingSystem` method (finally closes the connection). `throw new BookingException(...)` for: empty name, bad ticket count, seat already booked, invalid booking ID. `throws BookingException` on service methods and `DatabaseConnection.getConnection()` (connection failure). `NumberFormatException` caught in `BookingFrame`.

**Swing:** `JFrame`, `JPanel`, `JLabel`, `JTextField`, `JButton`, `JComboBox`, `JCheckBox` (seats), `JTable`, `JOptionPane` across the `gui` package.

**Event Handling:** each frame `implements ActionListener`, registers buttons with `addActionListener(this)`, and checks `e.getSource()` inside `actionPerformed()`.

**JDBC:** `DatabaseConnection` uses `DriverManager.getConnection`. Service methods use `Connection`, `PreparedStatement`, `ResultSet`.

**CRUD:**
- INSERT: `bookTicket()` (bookings, customers)
- SELECT: `getMovies()`, `getShows()`, `getBookedSeats()`, `getBookingById()`, `getBookingHistory()`
- UPDATE: `cancelBooking()` sets status to CANCELLED
- DELETE: `deleteBooking()` removes a cancelled record

## 5. Sample data (loaded by the SQL script)
- **Movies (8)**: Space Odyssey (Rs.200), Laugh Out Loud (150), The Last Detective (180), Wonder Kids (120), Interstellar (250), Inception (220), The Dark Knight (200), Avatar: The Way of Water (280). Each movie has 3 show times.
- **Customers (8)**: Anu, Rahul, Priya Patel, Amit Kumar, Sneha Reddy, Vikram Singh, Ananya Iyer, Rohan Verma.
- **Bookings (10)**: Includes both Cash and Card payments, with 8 active BOOKED bookings and 2 pre-set CANCELLED bookings (for testing record deletion). Bookings #1 (Anu, Space Odyssey 10:00 AM, A1,A2) and #2 (Rahul, Laugh Out Loud 11:00 AM, B3) match all initial test cases.

## 6. Simple test cases
| # | Action | Expected result |
|---|--------|-----------------|
| 1 | Book Tickets > Space Odyssey > 10:00 AM, name "Meera", tickets 2, seats A3,A4, Cash | Confirmation screen, total Rs. 400.00 |
| 2 | Same as 1 but Card | Total Rs. 408.00 (2% fee) |
| 3 | Leave name empty and book | Error: "Customer name cannot be empty." |
| 4 | Tickets = 0 or 9 | Error: "Number of tickets must be between 1 and 8." |
| 5 | Tickets = abc | "Please enter a valid number of tickets." |
| 6 | Tickets = 2 but select 1 seat | Error: "Please select exactly 2 seat(s)." |
| 7 | Space Odyssey 10:00 AM | A1 and A2 shown disabled "(booked)" (from sample booking #1) |
| 8 | History > type ID 999 > View Details | Error: "Invalid booking ID: 999" |
| 9 | History > type "abc" as ID > View Details | Error: "Booking ID must be a number." |
| 10 | History > select booking #1 > Cancel Booking > Yes | Status becomes CANCELLED; A1, A2 free again |
| 11 | Cancel booking #1 again | Error: "already cancelled" |
| 12 | Delete Record on a BOOKED booking | Error: "Only cancelled bookings can be deleted." |
| 13 | Cancel a booking, then Delete Record | Row disappears from history |
| 14 | History > search "anu" | Only Anu's bookings (overloaded method) |
| 15 | Stop MySQL or use wrong password, open Book Tickets | Error dialog: "Cannot connect to database..." |

## Optional: build with Maven (pom.xml)
Maven is used here only to download the MySQL driver and compile. No framework.
```
mvn compile exec:java
```
(Edit the password in `DatabaseConnection.java` first. With Maven you do not need to copy the JAR into `lib/`.)
