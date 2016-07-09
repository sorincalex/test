import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;

public class PrintText extends JFrame implements Printable {
	public static String FONTFAMILY = "Monospaced";
	public static int FONTSIZE = 10;
	public static int FONTSTYLE = Font.PLAIN;
	public static float LINESPACEFACTOR = 1.3f;
	
	public static final String logo = 
		"DACIA MEDICAL CENTER \n\t B-DUL DACIA 101, AP.1 , SECTOR 2 , BUCURESTI ; TEL: 211.54.05";

	/**
	* The text to be printed.
	*/
	private String mText;

	/**
	 * Our text in a form for which we can obtain a
	 * AttributedCharacterIterator.
	 */
	private ArrayList mStyledText = new ArrayList();
	
	int linespacing;
	int linesPerPage;
	int pageNumber;
	int numPages;
	Font font;
	PageFormat format;
	int linesPerFirstPage;
	
	/**
	 * Print a single page containing some sample text.
	 */
	
	public PrintText (String text, PageFormat format, int pageNumber) {
		//this.linesPerPage = linesPerPage;
		this.pageNumber = pageNumber;
		mText = insertSpaces(text);
		
		StringTokenizer st = new StringTokenizer(mText, "\n");
		while (st.hasMoreTokens()) {
			String item = st.nextToken();
			System.out.println("**** " + item + " ****");
			mStyledText.add(new AttributedString(item));
		}
		font = new Font(FONTFAMILY, FONTSTYLE, FONTSIZE);
		linespacing = (int) (FONTSIZE * LINESPACEFACTOR);
		this.format = format;
		
		Image img = getToolkit().getImage("c:\\Chadi\\dacia.JPG");
		int imgWidth = img.getWidth(this);
		int imgHeight = 100; //img.getHeight(this);
		System.out.println("imgHeight = " + imgHeight);
		linesPerFirstPage = (int)Math.floor((format.getImageableHeight() - imgHeight) / linespacing);
		//linesPerFirstPage /= 2;
		linesPerPage = (int)Math.floor(format.getImageableHeight() / linespacing);
		//linesPerPage /= 2;
		if (mStyledText.size() < linesPerFirstPage)
			numPages = 1;
		else 
			numPages = 2 + (mStyledText.size() - 1 - linesPerFirstPage) / linesPerPage;
		System.out.println("CONSTRUCTOR: LINESPERPAGE = " + linesPerPage);
		System.out.println("CONSTRUCTOR: LINESPERFIRSTPAGE = " + linesPerFirstPage);
		System.out.println("CONSTRUCTOR: NUMBEROFPAGES = " + numPages);
	}
	
	public void setPageNumber(int i) {
		pageNumber = i;
	}
	
	public int getNumberOfPages() { return numPages; }
	//public PageFormat getPageFormat(int pagennum) { return format; }
	//public Printable getPrintable(int pagenum) { return this; }
	
	/**
	 * Print a page of text.
	 */
	public int print(Graphics g, PageFormat format, int pageIndex) {
				
		//if ((pageIndex < 0) || (pageIndex >= numPages)) 
		//	return NO_SUCH_PAGE;
		
		/* We'll assume that Jav2D is available.
		 */
		Graphics2D g2d = (Graphics2D) g;
		/* Move the origin from the corner of the Paper to the corner
		 * of the imageable area.
		 */
		g2d.translate(format.getImageableX(), format.getImageableY());
		/* Set the text color.
		 */

		g2d.setFont(font);
		g2d.setPaint(Color.black);

		linespacing = (int)(g.getFontMetrics().getHeight() * LINESPACEFACTOR);
		linesPerFirstPage = (int)Math.floor((format.getImageableHeight() - 100) / linespacing);
		//linesPerFirstPage /= 2;
		linesPerPage = (int)Math.floor(format.getImageableHeight() / linespacing);
		//linesPerPage /= 2;
		if (mStyledText.size() < linesPerFirstPage)
			numPages = 1;
		else 
			numPages = 2 + (mStyledText.size() - 1 - linesPerFirstPage) / linesPerPage;
		System.out.println("LINESPERPAGE = " + linesPerPage);
		System.out.println("LINESPERFIRSTPAGE = " + linesPerFirstPage);
		System.out.println("NUMBEROFPAGES = " + numPages);
		
		Image img = null;
		if (pageNumber == 0) {
			img = getToolkit().getImage("c:\\Chadi\\dacia.JPG");
			int imgWidth = img.getWidth(this);
			int imgHeight = img.getHeight(this);
			int x = (getSize().width - imgWidth) / 2;
			int y = 2 * g.getFontMetrics().getHeight();

			//g.fillRect(x - 2, y - 2, imgWidth + 4, imgHeight + 4);
			if (img != null) 
				g2d.drawImage(img, 0, 0, this);
		}
		
		Point2D.Float pen = new Point2D.Float();
		//Font font = new Font("LucidaSans", Font.PLAIN, 40);
		
		int startLine = pageNumber * ((pageNumber == 1) ? linesPerFirstPage : linesPerPage);
		int endLine = startLine + ((pageNumber == 0) ? linesPerFirstPage : linesPerPage) - 1;
		if (endLine >= mStyledText.size())
			endLine = mStyledText.size() - 1;
		System.out.println("STARTLINE = " + startLine);
		System.out.println("ENDLINE = " + endLine);
		
		for (int i = startLine; i <= endLine; i++) {
			AttributedCharacterIterator charIterator = 
				((AttributedString)mStyledText.get(i)).getIterator();
			LineBreakMeasurer measurer = 
				new LineBreakMeasurer(charIterator, g2d.getFontRenderContext());
			float wrappingWidth = (float) format.getImageableWidth(); 

			while (measurer.getPosition() < charIterator.getEndIndex()) {	
				TextLayout layout = measurer.nextLayout(wrappingWidth);
				pen.y += layout.getAscent();
				float dx = layout.isLeftToRight()? 0 : (wrappingWidth - layout.getAdvance());
				System.out.println("pen.y = " + pen.y);
				if (pageNumber == 0) {
					layout.draw(g2d, pen.x + dx, pen.y + img.getHeight(this));
				} else {
					layout.draw(g2d, pen.x + dx, pen.y);
				}
				pen.y += layout.getDescent() + layout.getLeading() + 2;
			}
		}
		System.out.println("**** linesPerPage = " + linesPerPage);

		return Printable.PAGE_EXISTS;
	}
	
	private String insertSpaces(String text) {
		StringBuffer string = new StringBuffer(text);
		for (int i = 1; i < string.length(); i++) {
			if ((string.charAt(i) == '\n') && (string.charAt(i - 1) == '\n')) {
				string.insert(i, " ");
			}
		}
		return string.toString();
	}
}
