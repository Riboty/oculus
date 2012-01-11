package oculus;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.*;
import javax.servlet.http.*;

import org.red5.io.amf3.ByteArray;

public class FrameGrabHTTP extends HttpServlet {
	
	private static Application app = null;
	public static byte[] img  = null;
	private State state = State.getReference();
	
	public static void setApp(Application a) {
		if(app != null) return;
		app = a;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req,res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		res.setContentType("image/jpeg");
		OutputStream out = res.getOutputStream();

			System.out.println("OCULUS: frame grabbing servlet start");
			img = null;
			if (app.frameGrab()) {
				
				int n = 0;
				while (state.getBoolean(State.framegrabbusy)) {
//				while (img == null) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
					n++;
					if (n> 2000) {  // give up after 10 seconds 
						state.set(State.framegrabbusy, false);
						break;
					}
				}
//				System.out.println("OCULUS: frame byte size: "+img.length());
				System.out.println("OCULUS: frame grabbing done in "+n*5+" ms");
				
				if (img != null) {
					for (int i=0; i<img.length; i++) {
						out.write(img[i]);
					}
				}
			    out.close();
			}

	}
	
}