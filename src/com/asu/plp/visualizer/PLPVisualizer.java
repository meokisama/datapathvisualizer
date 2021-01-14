package com.asu.plp.visualizer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import org.json.JSONObject;

import com.asu.plp.communication.BackendProducer;
import com.asu.plp.communication.FrontendConsumer;
import com.asu.plp.event.SnapshotEventHandler;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;

public class PLPVisualizer extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	/** 
	 * Default Constructor
	 */
	public PLPVisualizer()
	{
		super("PLP Visualizer");

		myGraph graph = new myGraph();
		Object parent = graph.getDefaultParent();

		String conf_file = "conf/graph_blue_print.json";
		BufferedReader reader;
		String line = null;
		String jsonString = "";
		try {
			reader = new BufferedReader(new FileReader (conf_file));
			while((line = reader.readLine()) != null) {
				jsonString += line;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject conf = new JSONObject(jsonString);
		JSONObject vertices = conf.getJSONObject("vertices");
		JSONObject edges = conf.getJSONObject("edges");

		graph.getModel().beginUpdate();
		try
		{
			// create nodes
			Iterator<?> json_keys = vertices.keys();
			
			while( json_keys.hasNext() ){
				String json_key = (String)json_keys.next();
				JSONObject node = vertices.getJSONObject(json_key);
				graph.insertVertex(parent, node.getString("id"), 
										(Object)node.getString("name"),
										node.getDouble("pos_x"),
										node.getDouble("pos_y"),
										node.getDouble("width"),
										node.getDouble("height"));
			}

			// create edges
			json_keys = edges.keys();
			
			while( json_keys.hasNext() ){
				String json_key = (String)json_keys.next();
				JSONObject node = edges.getJSONObject(json_key);
				Object first_node = ((mxGraphModel)graph.getModel()).getCell(
											node.getString("vertex_id_from"));
				Object second_node = ((mxGraphModel)graph.getModel()).getCell(
											node.getString("vertex_id_to"));
				graph.insertEdge(parent, node.getString("id"),
										(Object)node.getString("name"),
										first_node, second_node,
										node.getString("style"));
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		graphComponent.setToolTips(true);
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				if (cell != null)
				{
					System.out.println("cell="+graph.getLabel(cell));
				}
			}
			public void mouseEntered(MouseEvent e)
			{
				System.out.println("Hover");
			}
			public void mouseExited(MouseEvent e)
			{
				System.out.println("Leave");
			}
			public void mouseMoved(MouseEvent e)
			{
				System.out.println("Moved");
			}
		});
		FrontendConsumer frontend = new FrontendConsumer();
		frontend.addListener(new SnapshotEventHandler(){
			public void receiveSnapshot(String jsonString)
			{
				JSONObject snapshot = new JSONObject(jsonString);
				JSONObject vertices = snapshot.getJSONObject("vertices_values");
				Iterator<?> json_keys = vertices.keys();
				ArrayList<Object> enabled_list = new ArrayList<Object>();
				ArrayList<Object> disabled_list = new ArrayList<Object>();

				while( json_keys.hasNext() ) {
					String json_key = (String)json_keys.next();
					JSONObject node = vertices.getJSONObject(json_key);
					mxCell myCell = (mxCell) ((mxGraphModel)graph.getModel()).getCell(json_key);
					myCell.setValue(node);
				}

				JSONObject edges = snapshot.getJSONObject("enabled_edges");
				json_keys = edges.keys();

				while( json_keys.hasNext() ) {
					String json_key = (String)json_keys.next();
					int enabled = edges.getInt(json_key);
					Object myCell = ((mxGraphModel)graph.getModel()).getCell(json_key);
					if (enabled == 1)
						enabled_list.add(myCell);
					else
						disabled_list.add(myCell);
				}
				graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", enabled_list.toArray());
				graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "green", disabled_list.toArray());
			}
		});
		thread(frontend, false);
	}

	public static void main(String[] args)
	{
		PLPVisualizer frame = new PLPVisualizer();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		thread(new BackendProducer(), false);
	}
	
	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

}
