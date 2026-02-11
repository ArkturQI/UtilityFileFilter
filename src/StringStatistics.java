class StringStatistics {
    private int count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public void add(String value) {
        count++;
        int length = value.length();
        if (length < minLength) minLength = length;
        if (length > maxLength) maxLength = length;
    }

    public int getCount() { return count; }


    public String getShortStats() {
        return "Количество: " + count;
    }

    public String getFullStats() {
        if (count == 0) return "Нет данных";
        return String.format("Количество: %d, Минимальная длина: %d, Максимальная длина: %d",
                count, minLength, maxLength);
    }
}