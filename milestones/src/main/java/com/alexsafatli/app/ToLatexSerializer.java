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
	protected final Map<String,ReferenceNode> refs = 
		new HashMap<String,ReferenceNode>();
	protected final Map<String,String> abbreviations = 
		new HashMap<String,String>();
	protected final LinkRenderer linkRenderer;

	// TODO Documentation (Verbatim?)
	protected LatexVerbatimSerializer verbSerializer =
		new LatexVerbatimSerializer();

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
		for (ReferenceNode refNode : node.getReferences()) {
			visitChildren(refNode);
			references.put(normalize(printer.getString()),refNode);
			printer.clear();
		}
		for (AbbreviationNode abbrNode : node.getAbbreviations()) {
			visitChildren(abbrNode);
			String abbr = printer.getString();
			printer.clear();
			abbrNode.getExpansion().accept(this);
			String exp = printer.getString();
			abbreviations.put(abbr,ext);
			printer.clear();
		}
		visitChildren(node);
	}

	public void visit(AbbreviationNode node) {
		// TODO Look up AbbreviationNode.
	}

	public void visit(AnchorLinkNode node) {
		printLink(linkRenderer.render(node));
	}

	public void visit(AutoLinkNode node) {
		printLink(linkRenderer.render(node));
	}

	public void visit(BlockQuoteNode node) {
		printBlock(node,"quote"); // This is correct?
	}

	public void visit(BulletListNode node) {
		printListItem(node);
	}

	public void visit(CodeNode node) {
		printBlock(node,"code"); // This is correct?
	}

	public void visit(DefinitionListNode node) {
		printBlock(node,""); // TODO Figure out what analogue for this.
	}

	public void visit(DefinitionNode node) {
		printBlock(node,""); // TODO Figure out what analogue for this.
	}

	public void visit(DefinitionTermNode node) {
		printBlock(node,""); // TODO Figure out what analogue for this.
	}

	public void visit(ExpImageNode node) {
		String text = printChildrenToString(node);
		// TODO What is this?
	}

	public void visit(ExpLinkNode node) {
		String text = printChildrenToString(node);
		// TODO What is this?		
	}

	public void visit(HeaderNode node) {
		String sub = "";
		for (int i = node.getLevel(); i > 0; --i) {
			sub += "sub";
		}
		printSection(node,sub + "section");
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
		printer.println().print(node.getText());
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

}