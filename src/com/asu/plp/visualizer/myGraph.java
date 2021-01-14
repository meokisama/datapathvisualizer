package com.asu.plp.visualizer;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class myGraph extends mxGraph{
	
	public static final NumberFormat numberFormat = NumberFormat.getInstance();
	
	public String getToolTipForCell(Object cell)
	{
		String tip = "<html>";
		mxGeometry geo = getModel().getGeometry(cell);
		mxCellState state = getView().getState(cell);
		mxCell mycell = (mxCell)cell;

		if (getModel().isEdge(cell))
		{
			tip += "points={";

			if (geo != null)
			{
				List<mxPoint> points = geo.getPoints();

				if (points != null)
				{
					Iterator<mxPoint> it = points.iterator();

					while (it.hasNext())
					{
						mxPoint point = it.next();
						tip += "[x=" + numberFormat.format(point.getX())
								+ ",y=" + numberFormat.format(point.getY())
								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}
			}

			tip += "}<br>";
			tip += "absPoints={";

			if (state != null)
			{

				for (int i = 0; i < state.getAbsolutePointCount(); i++)
				{
					mxPoint point = state.getAbsolutePoint(i);
					tip += "[x=" + numberFormat.format(point.getX())
							+ ",y=" + numberFormat.format(point.getY())
							+ "],";
				}

				tip = tip.substring(0, tip.length() - 1);
			}

			tip += "}";
		}
		else
		{
			tip += "geo=[";

			if (geo != null)
			{
				tip += "x=" + numberFormat.format(geo.getX()) + ",y="
						+ numberFormat.format(geo.getY()) + ",width="
						+ numberFormat.format(geo.getWidth()) + ",height="
						+ numberFormat.format(geo.getHeight());
			}

			tip += "]<br>";
			tip += "state=[";

			if (state != null)
			{
				tip += "x=" + numberFormat.format(state.getX()) + ",y="
						+ numberFormat.format(state.getY()) + ",width="
						+ numberFormat.format(state.getWidth())
						+ ",height="
						+ numberFormat.format(state.getHeight());
			}

			tip += "]<br>";
			tip += mycell.getValue().toString().replaceAll(",", "<br>");
		}

		/*mxPoint trans = getView().getTranslate();

		tip += "<br>scale=" + numberFormat.format(getView().getScale())
				+ ", translate=[x=" + numberFormat.format(trans.getX())
				+ ",y=" + numberFormat.format(trans.getY()) + "]";*/
		tip += "</html>";

		return tip;
	}
}
