class NumberStatistics<T extends Number & Comparable<T>> {
    private int count = 0;
    private T min = null;
    private T max = null;
    private double sum = 0.0;

    public void add(T value) {
        count++;
        sum += value.doubleValue();

        if (min == null || value.compareTo(min) < 0) {
            min = value;
        }
        if (max == null || value.compareTo(max) > 0) {
            max = value;
        }
    }

    public int getCount() { return count; }
    public double getAverage() { return count > 0 ? sum / count : 0.0; }

    public String getShortStats() {
        return "Количество: " + count;
    }

    public String getFullStats() {
        if (count == 0) return "Нет данных";
        return String.format("Количество: %d, Мин: %s, Макс: %s, Сумма: %.2f, Среднее: %.2f",
                count, min, max, sum, getAverage());
    }
}