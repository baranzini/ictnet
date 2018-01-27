package org.cytoscape.ictnet2.internal;

public class Stats {
	
	/* Most methods in this class are copied from Andrew McCallum's code.
	 * Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
	   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
	   http://www.cs.umass.edu/~mccallum/mallet
	   This software is provided under the terms of the Common Public License,
	   version 1.0, as published by http://www.opensource.org.  For further
	   information, see the file `LICENSE' included with this distribution. */

	/** 
	 @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
	 */	

	public static double pchisq(double q, double df) {
	    // Posten, H. (1989) American Statistician 43 p. 261-265
	    double df2 = df * .5;
	    double q2 = q * .5;
	    int n = 5, k;
	    double tk, CFL, CFU, prob;
	    if (q <= 0 || df <= 0)
	      throw new IllegalArgumentException("Illegal argument " + q + " or "
	          + df + " for qnorm(p).");
	    if (q < df) {
	      tk = q2 * (1 - n - df2)
	          / (df2 + 2 * n - 1 + n * q2 / (df2 + 2 * n));
	      for (k = n - 1; k > 1; k--)
	        tk = q2 * (1 - k - df2)
	            / (df2 + 2 * k - 1 + k * q2 / (df2 + 2 * k + tk));
	      CFL = 1 - q2 / (df2 + 1 + q2 / (df2 + 2 + tk));
	      prob = Math.exp(df2 * Math.log(q2) - q2 - logGamma(df2 + 1)
	          - Math.log(CFL));
	    } else {
	      tk = (n - df2) / (q2 + n);
	      for (k = n - 1; k > 1; k--)
	        tk = (k - df2) / (q2 + k / (1 + tk));
	      CFU = 1 + (1 - df2) / (q2 + 1 / (1 + tk));
	      prob = 1 - Math.exp((df2 - 1) * Math.log(q2) - q2 - logGamma(df2)
	          - Math.log(CFU));
	    }
	    return prob;
	  }//
	
	// From libbow, dirichlet.c
	  // Written by Tom Minka <minka@stat.cmu.edu>
	  public static double logGamma(double x) {
	    double result, y, xnum, xden;
	    int i;
	    final double d1 = -5.772156649015328605195174e-1;
	    final double p1[] = { 4.945235359296727046734888e0,
	        2.018112620856775083915565e2, 2.290838373831346393026739e3,
	        1.131967205903380828685045e4, 2.855724635671635335736389e4,
	        3.848496228443793359990269e4, 2.637748787624195437963534e4,
	        7.225813979700288197698961e3 };
	    final double q1[] = { 6.748212550303777196073036e1,
	        1.113332393857199323513008e3, 7.738757056935398733233834e3,
	        2.763987074403340708898585e4, 5.499310206226157329794414e4,
	        6.161122180066002127833352e4, 3.635127591501940507276287e4,
	        8.785536302431013170870835e3 };
	    final double d2 = 4.227843350984671393993777e-1;
	    final double p2[] = { 4.974607845568932035012064e0,
	        5.424138599891070494101986e2, 1.550693864978364947665077e4,
	        1.847932904445632425417223e5, 1.088204769468828767498470e6,
	        3.338152967987029735917223e6, 5.106661678927352456275255e6,
	        3.074109054850539556250927e6 };
	    final double q2[] = { 1.830328399370592604055942e2,
	        7.765049321445005871323047e3, 1.331903827966074194402448e5,
	        1.136705821321969608938755e6, 5.267964117437946917577538e6,
	        1.346701454311101692290052e7, 1.782736530353274213975932e7,
	        9.533095591844353613395747e6 };
	    final double d4 = 1.791759469228055000094023e0;
	    final double p4[] = { 1.474502166059939948905062e4,
	        2.426813369486704502836312e6, 1.214755574045093227939592e8,
	        2.663432449630976949898078e9, 2.940378956634553899906876e10,
	        1.702665737765398868392998e11, 4.926125793377430887588120e11,
	        5.606251856223951465078242e11 };
	    final double q4[] = { 2.690530175870899333379843e3,
	        6.393885654300092398984238e5, 4.135599930241388052042842e7,
	        1.120872109616147941376570e9, 1.488613728678813811542398e10,
	        1.016803586272438228077304e11, 3.417476345507377132798597e11,
	        4.463158187419713286462081e11 };
	    final double c[] = { -1.910444077728e-03, 8.4171387781295e-04,
	        -5.952379913043012e-04, 7.93650793500350248e-04,
	        -2.777777777777681622553e-03, 8.333333333333333331554247e-02,
	        5.7083835261e-03 };
	    final double a = 0.6796875;

	    if ((x <= 0.5) || ((x > a) && (x <= 1.5))) {
	      if (x <= 0.5) {
	        result = -Math.log(x);
	        /* Test whether X < machine epsilon. */
	        if (x + 1 == 1) {
	          return result;
	        }
	      } else {
	        result = 0;
	        x = (x - 0.5) - 0.5;
	      }
	      xnum = 0;
	      xden = 1;
	      for (i = 0; i < 8; i++) {
	        xnum = xnum * x + p1[i];
	        xden = xden * x + q1[i];
	      }
	      result += x * (d1 + x * (xnum / xden));
	    } else if ((x <= a) || ((x > 1.5) && (x <= 4))) {
	      if (x <= a) {
	        result = -Math.log(x);
	        x = (x - 0.5) - 0.5;
	      } else {
	        result = 0;
	        x -= 2;
	      }
	      xnum = 0;
	      xden = 1;
	      for (i = 0; i < 8; i++) {
	        xnum = xnum * x + p2[i];
	        xden = xden * x + q2[i];
	      }
	      result += x * (d2 + x * (xnum / xden));
	    } else if (x <= 12) {
	      x -= 4;
	      xnum = 0;
	      xden = -1;
	      for (i = 0; i < 8; i++) {
	        xnum = xnum * x + p4[i];
	        xden = xden * x + q4[i];
	      }
	      result = d4 + x * (xnum / xden);
	    }
	    /* X > 12 */
	    else {
	      y = Math.log(x);
	      result = x * (y - 1) - y * 0.5 + .9189385332046727417803297;
	      x = 1 / x;
	      y = x * x;
	      xnum = c[6];
	      for (i = 0; i < 6; i++) {
	        xnum = xnum * y + c[i];
	      }
	      xnum *= x;
	      result += xnum;
	    }
	    return result;
	  }

	  public static double calculateMinusLogFisherPvalue(String pvalueStr){
			String[] pvalueStrs = pvalueStr.split(",");			
			int arraySize = pvalueStrs.length;
			if (arraySize > 1){
				double sumLogP = 0.0;
				for(String pStr: pvalueStrs){
					double p = Double.parseDouble(pStr);
					sumLogP += Math.log10(p);			
				}//for
				double Xsq = (-2)*sumLogP;			
				double FisherP = 1 - pchisq(Xsq, 2*arraySize);
				//if (FisherP == 0.0)
				//	System.out.println(Xsq+ ":"+ 2*arraySize + ":"+ pvalueStr);
						
				return (-1)*Math.log10(FisherP);
			}//if
			
			return (-1)*Math.log10(Double.parseDouble(pvalueStr));			
	   }
	  
	  public static double calculateMaxValue(String valueStr){
		  String[] valueStrs = valueStr.split(",");
		  double max = -1.0;
		  int arraySize = valueStrs.length;
		  if (arraySize > 1){			 
			  for(String vStr: valueStrs){
				  if (vStr == "NR")
					  continue;
				  try{
				      double v = Double.parseDouble(vStr);
				      if (v > max)
					      max = v;
				  }catch(Exception ex){
					  // do nothing, just ignore
				  }//try-catch
			  }//for
		  }//if
		  return max;
	  }

	  
	  /*//test
	  public static void main(String [ ] args) {
		  String pvalueStr = "0.01,0.2,0.3";
		  double minusLogP = calculateMinusLogFisherPvalue(pvalueStr);		  
	  }*/

}
