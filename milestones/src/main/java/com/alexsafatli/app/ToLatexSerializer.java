/* Alexander Safatli
Date: May 2015
Email: safatli@cs.dal.ca
Description: Built as an extension of pegdown. Serializes Markdown to LaTeX.
*/

package com.alexsafatli.app;

import org.pegdown.Printer;
import org.pegdown.LinkRenderer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.*;

import java.util.HashMap;
import java.util.Map;

public class ToLatexSerializer implements Visitor {

	/* Attributes */

	// TODO Documentation (renderers, etc.)
	protected Printer printer = new Printer();
	protected final LinkRenderer linkRenderer;

	// TODO Documentation (cursors)
	protected TableNode currTableNode;
	protected int currTableCol;
	protected boolean inTableHeader;

	/* Functions */

	public ToLatexSerializer(LinkRenderer linkRenderer) {
		this.linkRenderer = linkRenderer;
	}

	public String toLatex(RootNode astRoot) {
		astRoot.accept(this);
		return printer.getString();
	}

	public void visit(RootNode node) {
        printPreamble();
        printBlock(node,"document"); // Wrap into a single document.
	}

	public void visit(AnchorLinkNode node) {
		printLink(linkRenderer.render(node));
	}

	public void visit(AutoLinkNode node) {
		printLink(linkRenderer.render(node));
	}

	public void visit(BlockQuoteNode node) {
		printBlock(node,"quote");
	}

	public void visit(BulletListNode node) {
		printListItem(node);
	}

	public void visit(CodeNode node) {
		printBlock(node,"verbatim");
	}

	public void visit(DefinitionListNode node) {
		printBlock(node,"description");
	}

	public void visit(DefinitionNode node) {
		// TODO
	}

	public void visit(DefinitionTermNode node) {
		// TODO
	}

	public void visit(ExpImageNode node) {
        // TODO
		/*String text = printChildrenToString(node);
		printImageTag(linkRenderer.render(node,text));*/
	}

	public void visit(ExpLinkNode node) {
        // TODO
		/*String text = printChildrenToString(node);
		printLink(linkRenderer.render(node,text));*/
	}

	public void visit(HeaderNode node) {
		printSection(node,node.getLevel());
	}

	public void visit(HtmlBlockNode node) { // Ignore.
	}

	public void visit(InlineHtmlNode node) { // Ignore.
	}

	public void visit(ListItemNode node) {
		printListItem(node);
	}

	public void visit(MailLinkNode node) {
		printLink(linkRenderer.render(node));
	}

	public void visit(OrderedListNode node) {
		printBlock(node,"itemize");
	}

	public void visit(ParaNode node) {
		printer.println();
        visitChildren(node);
	}

    public void visit(QuotedNode node) {
        switch (node.getType()) {
            case DoubleAngle:
                printer.print("\\flqq");
                visitChildren(node);
                printer.print("\\frqq");
                break;
            case Double:
                printer.print("``");
                visitChildren(node);
                printer.print("\"");
                break;
            case Single:
                printer.print("`");
                visitChildren(node);
                printer.print("'");
                break;
        }
    }

    public void visit(ReferenceNode node) {
    }

    public void visit(RefImageNode node) {
    	// TODO Figure out what this is.
    }

    public void visit(RefLinkNode node) {
    	// TODO Figure out what this is.
    }

    public void visit(SimpleNode node) {
        switch (node.getType()) {
            case Apostrophe:
                printer.print("'");
                break;
            case Ellipsis:
                printer.print("...");
                break;
            case Emdash:
                printer.print("---");
                break;
            case Endash:
                printer.print("--");
                break;
            case HRule:
                printer.println().print("\\spacedhrule{-0.2em}{-0.4em}").
                	println();
                break;
            case Linebreak:
                printer.println();
                break;
            case Nbsp:
                printer.print("~");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void visit(StrongEmphSuperNode node) {
    	if (node.isClosed()) {
    		if (node.isStrong()) printTag(node,"textbf");
    		else printTag(node,"textit");
    	}
    	else {
    		printer.print(node.getChars());
    		visitChildren(node);
    	}
    }

    public void visit(StrikeNode node) {
    	printTag(node,"textit"); // TODO For now, italicize.
    }

    public void visit(TableBodyNode node) {
    	printBlock(node,"table");
    }

    public void visit(TableCaptionNode node) {
        printTag(node,"caption");
    }

    public void visit(TableCellNode node) {
        // TODO
    }

    public void visit(TableColumnNode node) {

    }

    public void visit(TableHeaderNode node) {

    }

    public void visit(TableNode node) {

    }

    public void visit(TableRowNode node) {

    }

    public void visit(VerbatimNode node) {
    	printBlock(node,"verbatim");
    }

    public void visit(WikiLinkNode node) {
    	printLink(linkRenderer.render(node));
    }

    public void visit(TextNode node) {
        printer.print(node.getText());
    }

    public void visit(SpecialTextNode node) {
        printer.printEncoded(node.getText());
    }

    public void visit(SuperNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(Node node) {
    }

    @Override
    public void visit(AbbreviationNode node) {
    }

    protected void visitChildren(SuperNode node) {
        for (Node child : node.getChildren()) {
            child.accept(this);
        }
    }

    protected void printPreamble() {
        printer.print("\\documentclass[12pt]{article}").println();
    }

    protected void printTag(SuperNode node, String name) {
        printer.print("\\" + name).print("{");
        visitChildren(node);
        printer.print("}");
    }

    protected void printTag(TextNode node, String name) {
        printer.print("\\" + name).print("{");
        printer.print(node.getText()).print("}");
    }

    protected void printTag(SuperNode node, String name, String arg) {
        printer.print("\\" + name).print("[").print(arg).print("]").print("{");
        visitChildren(node);
        printer.print("}");
    }

    protected void printTag(TextNode node, String name, String arg) {
        printer.print("\\" + name).print("[").print(arg).print("]").print("{");
        printer.print(node.getText()).print("}");
    }

    protected void printLink(LinkRenderer.Rendering rendering) {
        printer.print("\\href{" + rendering.href + "}{" + rendering.text + "}");
    }

    protected void printBlock(SuperNode node, String name) {
        printer.println().print("\\begin{" + name + "}").println().indent(+2);
        visitChildren(node);
        printer.indent(-2).println().print("\\end{" + name + "}").println();
    }

    protected void printBlock(TextNode node, String name) {
        printer.println().print("\\begin{" + name + "}").println().indent(+2);
        printer.printEncoded(node.getText());
        printer.indent(-2).println().print("\\end{" + name + "}").println();
    }

    protected void printSection(SuperNode node, int level) {
        String sub = "";
        if (level > 1) {
            // Default "report" only allows three levels deep subsections.
            for (int i = 1; i <= 3 && i <= level; i++) sub += "sub";
        }
        printer.println().print("\\" + sub + "section{}").println();
        visitChildren(node);
    }

    protected void printSection(TextNode node, int level) {
        String sub = "";
        if (level > 1) {
            // Default "report" only allows three levels deep subsections.
            for (int i = 1; i <= 3 && i <= level; i++) sub += "sub";
        }
        printer.println().print("\\" + sub + "section{").print(node.getText()).
            print("}").println();
    }

    protected void printListItem(TextNode node) {
        printTag(node,"item");
    }

    protected void printListItem(TextNode node, String arg) {
        printTag(node,"item",arg);
    }

    protected void printListItem(SuperNode node) {
        printTag(node,"item");
    }

    protected void printListItem(SuperNode node, String arg) {
        printTag(node,"item",arg);
    }

    protected String printChildrenToString(SuperNode node) {
        Printer priorPrinter = printer;
        printer = new Printer();
        visitChildren(node);
        String result = printer.getString();
        printer = priorPrinter;
        return result;
    }

}