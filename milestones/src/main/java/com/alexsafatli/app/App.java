/* Entry point to application. */

package com.alexsafatli.app;
import org.pegdown.PegDownProcessor;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.RootNode;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        PegDownProcessor processor = new PegDownProcessor();
        RootNode astRoot = processor.parseMarkdown("*Hello World!*");
        LinkRenderer linkRenderer = new LinkRenderer();
        ToLatexSerializer serializer = new ToLatexSerializer(linkRenderer);
        System.out.println(serializer.toLatex(astRoot));
    }
}
