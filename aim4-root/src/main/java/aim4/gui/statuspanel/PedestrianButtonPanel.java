package aim4.gui.statuspanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import aim4.gui.StatusPanelInterface;
import aim4.gui.Viewer;

public class PedestrianButtonPanel extends JPanel implements StatusPanelInterface, ActionListener { 
	
	private static final long serialVersionUID = 1L;
	
	Viewer viewer;
	
	// Six buttons to represent six paths across an intersection. 
	private JButton left;
	private JButton right;
	private JButton top;
	private JButton bottom;
	private JButton topLeftToBottomRight;
	private JButton topRightToBottomLeft;
	
	public PedestrianButtonPanel(Viewer viewer) {
		
		left = new JButton("Left");
		right = new JButton("Right");
		top = new JButton("Top");
		bottom = new JButton("Bottom");
		topLeftToBottomRight = new JButton("Top Left to Bottom Right");
		topRightToBottomLeft = new JButton("Top Right to Bottom Left");
		
		this.viewer = viewer;
		
		// layout
		
	    GroupLayout layout = new GroupLayout(this);
	    setLayout(layout);
	    layout.setAutoCreateGaps(false);
	    layout.setAutoCreateContainerGaps(false);

	    layout.setHorizontalGroup(layout
	      .createSequentialGroup()
	        .addComponent(left)
	        .addComponent(right)
	        .addComponent(top)
	        .addComponent(bottom)
	        .addComponent(topLeftToBottomRight)
	        .addComponent(topRightToBottomLeft)
	        
	    );

	    layout.setVerticalGroup(layout
	      .createParallelGroup(GroupLayout.Alignment.CENTER)
	      .addComponent(left)
	        .addComponent(right)
	        .addComponent(top)
	        .addComponent(bottom)
	        .addComponent(topLeftToBottomRight)
	        .addComponent(topRightToBottomLeft)
	    );
	    
 
	    // add event handler
	    
	    
	    left.addActionListener(this);
	    right.addActionListener(this);
	    top.addActionListener(this);
	    bottom.addActionListener(this);
	    topLeftToBottomRight.addActionListener(this);
	    topRightToBottomLeft.addActionListener(this);
		
	}
		
	
	// Event handler
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == left) {
			
		}
		if (e.getSource() == right) {
			
		}
		if (e.getSource() == top) {
			
		}
		if (e.getSource() == bottom) {
			
		}
		if (e.getSource() == topLeftToBottomRight) {
			
		}
		if (e.getSource() == topRightToBottomLeft) {
			
		}
		
	}

	public void update() {
	    // do nothing
	  }
	
	public void clear() {
	    // do nothing
	  }

}

