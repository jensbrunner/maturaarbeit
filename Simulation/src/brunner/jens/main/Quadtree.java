package brunner.jens.main;

import java.util.concurrent.CopyOnWriteArrayList;

import brunner.jens.utils.Vector2;

public class Quadtree
{

	//The x,y coordinate of the top-left corner.
	public double x,y,sideLength;

	public Quadtree(double _x, double _y, double _sideLength)
	{
		//This is the x,y coordinate of the top-left corner
		x = _x;
		y = _y;
		sideLength = _sideLength;
	}

	public Quadtree ne = null;
	public Quadtree nw = null;
	public Quadtree se = null;
	public Quadtree sw = null;

	public CopyOnWriteArrayList<Body> bodies = new CopyOnWriteArrayList<>();

	public double totalMass;
	public Vector2 centerOfMass;

	public boolean containsBody(Body p) {
		if(p.position.x >= x && p.position.x < x + sideLength &&
		   p.position.y >= y && p.position.y < y + sideLength) return true;
		return false;
	}

	public void putBody(Body p)
	{
		// If this node is an internal
		if(bodies.size() > 1)
		{
			bodies.add(p);
			
			//Update the total mass and COM
			totalMass += p.mass;
			double com_x = 0f;
			double com_y = 0f;
			for(Body _p : bodies)
			{
				com_x += _p.position.x * _p.mass;
				com_y += _p.position.y * _p.mass;
			}
			centerOfMass = new Vector2(com_x/totalMass, com_y/totalMass);
			
			//Recursively insert
			if(ne.containsBody(p)) ne.putBody(p);
			if(nw.containsBody(p)) nw.putBody(p);
			if(se.containsBody(p)) se.putBody(p);
			if(sw.containsBody(p)) sw.putBody(p);

		}else if(bodies.size() == 0)
		{
			bodies.add(p);
			totalMass += p.mass;
		
		}else if(bodies.size() == 1)
		{
			//BarnesHut.numNodes += 4;
			bodies.add(p);
			totalMass += p.mass;
			
			double com_x = 0f;
			double com_y = 0f;
			for(Body _p : bodies)
			{
				com_x += _p.position.x * _p.mass;
				com_y += _p.position.y * _p.mass;
			}
			centerOfMass = new Vector2(com_x/totalMass, com_y/totalMass);
			
			//Create all 4 Subnodes
			ne = new Quadtree(x + sideLength/2, y, sideLength/2);
			nw = new Quadtree(x, y, sideLength/2);
			se = new Quadtree(x + sideLength/2, y + sideLength/2, sideLength/2);
			sw = new Quadtree(x, y + sideLength/2, sideLength/2);
			
			//Recursively insert.
			for(Body _p : bodies)
			{
				if(ne.containsBody(_p)) ne.putBody(_p);
				if(nw.containsBody(_p)) nw.putBody(_p);
				if(se.containsBody(_p)) se.putBody(_p);
				if(sw.containsBody(_p)) sw.putBody(_p);
			}
		}
	}
}
