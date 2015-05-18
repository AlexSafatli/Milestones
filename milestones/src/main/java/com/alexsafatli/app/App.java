/* Entry point to application. */

package com.alexsafatli.app;
import org.pegdown.PegDownProcessor;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        PegDownProcessor processor = new PegDownProcessor();
        System.out.println(processor.markdownToHtml("*Hello World!*"));
    }
}
