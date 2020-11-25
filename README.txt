Fibonacci, QuickSort, Hanoi (2020)

Aplikacja ma na celu pokazanie różnicy w czasie wykonywania operacji zaimplementowanych w dwóch podejściach: iteracyjnym i rekurencyjnym.

Zaimplementowano trzy operacje:
- obliczanie wybranego wyrazu ciągu Fibonacciego,
- sortowanie zestawu losowych liczb, o wybranej wielkości, za pomocą algorytmu QuickSort,
- układanie Wieży Hanoi, składającej z wybranej liczby krążków.

W przypadku każdej operacji istnieje możliwość wybrania zestawu pięciu, charakterystycznych dla danej operacji, danych wejściowych. Wybrane wartości nie powinny się powtarzać i występować w porządku rosnącym.

Dzięki wyborowi danych wejściowych, pojawia się możliwość porównania czasów wykonania, w zależności od poziomu złożoności danej operacji. W celu ustabilizowania otrzymanych czasów działania należy uruchomić obliczenia kilkukrotnie.

Czas wykonywania operacji, z podziałem na podejścia, prezentowany jest w postaci wykresów, tabeli oraz w konsoli tekstowej.

Aplikacja została napisana w język programowania Java. Do stworzenia interfejsu użytkownika wykorzystano wbudowaną bibliotekę Swing. Wykorzystano także dwie zewnętrzne zależności: XChart (https://knowm.org/open-source/xchart) - biblioteka służąca do generowania wykresów oraz Guice (https://github.com/google/guice) - framework zapewniający kontener zależności (ang. Dependency Injection Container), który pozwala na wygodne zarządzanie i wstrzykiwanie zależności.

Do uruchomienia aplikacji wymagane jest środowisko uruchomieniowe JRE. W przypadku systemu Windows należy uruchomić plik FQSH.exe. W przypadku innych systemów operacyjnych należy uruchomić plik JAR za pomocą polecenia: java -jar FQSH.jar.

Autorzy: Mateusz Liber, Przemysław Lyschik
