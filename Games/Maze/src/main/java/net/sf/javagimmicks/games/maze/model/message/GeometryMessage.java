package net.sf.javagimmicks.games.maze.model.message;

public class GeometryMessage implements Message
{
	private final int m_iGeometry;

	public GeometryMessage(final int iGeometry)
	{
		m_iGeometry = iGeometry;
	}

	public String getMessageString()
	{
		return "GEOMETRY " + String.valueOf(m_iGeometry);
	}
}
