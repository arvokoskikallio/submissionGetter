import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main
{

    public static String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static void main(String args[]) throws IOException {

        //Ask for the url as input
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter URL of the forum page:");
        String url = myObj.nextLine();

        System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe"); //CHANGE THIS DIRECTORY TO POINT TO CHROMEDRIVER
        ChromeDriver driver = new ChromeDriver();
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.className("message"));

        //change the world
        String finalMessage = "";

        int i;
        int j;
        int k;

        for(i=0; i<elements.size(); i++)
        {
            String message = elements.get(i).getText();
            String[] lines = message.split("\n");

            //if name is before date
            if(lines[0].toLowerCase().contains("name") && lines[1].toLowerCase().contains("date"))
            {
                //swaps the name and date
                String temp = lines[0];
                lines[0] = lines[1];
                lines[1] = temp;
            }

            //fixes missing date
            if(lines[0].toLowerCase().contains("name"))
            {
                lines[1] = lines[0];
                lines[0] = "Date: MISSING?";
            }


            for(j=0; j<lines.length; j++)
            {

                //HEADER PARSE
                if(lines[j].toLowerCase().contains("date"))
                {
                    lines[j] = "\n" + lines[j];

                    //converts lowercase months to uppercase
                    for(k=0; k<months.length; k++)
                    {
                        if(lines[j].contains(months[k].toLowerCase()))
                        {
                            lines[j] = lines[j].replace(months[k].toLowerCase(), months[k]);
                        }
                        else if(lines[j].contains(shortMonths[k].toLowerCase()))
                        {
                            lines[j] = lines[j].replace(shortMonths[k].toLowerCase(), shortMonths[k]);
                        }
                    }

                    //fixes long months
                    for(k=0; k<months.length; k++)
                    {
                        if(lines[j].contains(months[k]))
                        {

                        }
                        if(lines[j].contains(months[k]))
                        {
                            lines[j] = lines[j].replace(months[k], shortMonths[k]);
                        }
                    }

                    //removes useless days
                    for(k=0; k<days.length; k++)
                    {
                        if(lines[j].contains(days[k]))
                        {
                            lines[j] = lines[j].replace(days[k], "");
                        }
                    }

                    //Removes the time that people add at the end of the date
                    if(lines[j].toLowerCase().contains("am") || lines[j].toLowerCase().contains("pm"))
                    {
                        lines[j] = lines[j].substring(0, lines[j].length()-7);
                    }

                    //removes double spaces
                    lines[j] = lines[j].replace("  ", " ");

                    if(lines[j].contains("date"))
                    {
                        lines[j] = lines[j].replace("date", "Date");
                    }

                    //Fixes the "Paul Allain" error
                    if(lines[j].contains("Date :"))
                    {
                        lines[j] = lines[j].replace("Date :", "Date:");
                    }

                    if(lines[j].contains("Date;"))
                    {
                        lines[j] = lines[j].replace("Date;", "Date:");
                    }


                    //END OF DATE PARSE
                }


                if(lines[j].toLowerCase().contains("name"))
                {
                    //removes double spaces
                    lines[j] = lines[j].replace("  ", " ");

                    if(lines[j].contains("name"))
                    {
                        lines[j] = lines[j].replace("name", "Name");
                    }

                    //Fixes the "Paul Allain" error
                    if(lines[j].contains("Name :"))
                    {
                        lines[j] = lines[j].replace("Name :", "Name:");
                    }

                    if(lines[j].contains("Name;"))
                    {
                        lines[j] = lines[j].replace("Name;", "Name:");
                    }

                    //Adds space to combat parser crashes
                    lines[j] += "\n";

                    //END OF NAME PARSE
                }

                //If there's no name field
                if(lines[j].contains("Date:") && !lines[j+1].toLowerCase().contains("name"))
                {
                    lines[j+1] = "Name: MISSING?\n" + lines[j+1];
                }

                //fixes ng stuff
                if(lines[j].toLowerCase().contains(" ng") || lines[j].toLowerCase().contains("(ng)"))
                {
                    lines[j] = lines[j].replace("ng:", "nosc:");
                    lines[j] = lines[j].replace("NG:", "nosc:");
                    lines[j] = lines[j].replace("NG f", "nosc f");
                    lines[j] = lines[j].replace("NG F", "nosc F");
                    lines[j] = lines[j].replace("ng f", "nosc f");
                    lines[j] = lines[j].replace("ng F", "nosc F");
                    lines[j] = lines[j].replace(" ng ", " nosc ");
                    lines[j] = lines[j].replace(" NG ", " nosc ");
                    lines[j] = lines[j].replace("(NG)", "nosc");
                    lines[j] = lines[j].replace("(ng)", "nosc");
                }

                //Adds a warning flag for common incorrectly submitted tracks
                if((lines[j].toLowerCase().contains("rbc") && !lines[j].toLowerCase().contains("bc3")
                        || lines[j].toLowerCase().contains("rr") || lines[j].toLowerCase().contains("sgb")
                        || lines[j].toLowerCase().contains("rpg") || lines[j].toLowerCase().contains("tf")
                        || lines[j].toLowerCase().contains("rws") || lines[j].toLowerCase().contains("kc")) &&
                        !lines[j].toLowerCase().contains("date") && !lines[j].toLowerCase().contains("name")) //doesn't miscorrect date and name fields
                {
                    lines[j] += "  (POSSIBLE NG FLAG MISSING?)";
                }

            }

            //builds the final string
            for(j=0; j<lines.length; j++)
            {
                finalMessage += lines[j] + "\n";
                if(lines[j].equals(""))
                {
                    finalMessage += "\n";
                }
            }
        }


        //the date check ruins my message LOL
        String[] batches = finalMessage.split("Times upDated[.]");
        finalMessage = batches[batches.length-1];


        StringSelection selection = new StringSelection(finalMessage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        driver.close();
    }
}
