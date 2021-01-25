import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Document getPage() throws IOException {   //возвратит HTML код страницы
        String url = "https://www.pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);  //тут требуется обработать исключение
        return page;
    }

    //нам требуется чтобы в начале было две цифры, потом точка, потом еще две цифры (число.месяц)
    //в регулярных выражениях - \d(2)\.\d(2) это два символа, точка, потом опять два символа
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}"); //Шаблон, по которому надо искать

    private static String getDateFromString(String stringDate) throws Exception {  //метод регулярных выражений. Для того чтоб получить только дату, указанную в ячейке
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find())
            return matcher.group(); //если нашел то сгрупируй найденное
        throw new Exception("Can't extract date from string");
    }

    //метод для печати занчений температуры
    private static int printPartValues(Elements values, int index) { //index - это начало, с которого надо делать печать. метод вернет число напечатанных строк
        int iterationCount = 4;
        if (index == 0) {
            Element valueLn = values.get(0);
            if (valueLn.text().contains("День"))
                iterationCount = 3;
            if (valueLn.text().contains("Вечер"))
                iterationCount = 2;
            if (valueLn.text().contains("Ночь"))
                iterationCount = 1;
        }
        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) //все эллементы "td" из valueLine. не весь html код, а только td
                System.out.print(td.text() + "|||||");
            System.out.println();
        }
        return iterationCount; //вернуть количество итераций
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();   //HTML код страницы
        //css query language. Для того чтобы достать table со страницы. Table на этом сайте это таблица, в которой все нужные нам данные
        Element tableWth = page.select("table[class=wt]").first();
        Elements names = tableWth.select("tr[class=wth]"); //получить из tableWth только те tr, у которых class = wth
        Elements values = tableWth.select("tr[valign=top]"); //получить из tableWth только те tr, у которых valign = top
        int index = 0; //строки из values. Это начало строки с которой начнется печать
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text(); //текст ячейки с тегом <th>, где id=dt
            String date = getDateFromString(dateString); //форматирование текста с использованием регулярных выражений
            System.out.println(date + "   явление       температура     давление     влажность      ветер");
            //вывод значений по индексу
            int iterationCount = printPartValues(values, index);
            index += iterationCount;
        }
    }
}
