package org.apache.jmeter.threads;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class ListenerNotifier extends LinkedList implements Runnable
{
	private static Logger log =
		LoggingManager.getLoggerFor(JMeterUtils.ENGINE);
	/**
	 * @see java.lang.Runnable#run()
	 */
	boolean running;
	boolean isStopped;
	static private int ABS_MAX = 500;
	static private int MAX = 200;
	int sleepTime = 2000;
	public ListenerNotifier()
	{
		super();
		running = true;
		isStopped = true;
	}
	public void run()
	{
		Iterator iter;
		while (running || this.size() > 1)
		{
			SampleEvent res = (SampleEvent) this.removeFirst();
			if (res != null)
			{
				List listeners = (List) this.removeFirst();
				notifyListeners(res, listeners);
			}
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
			}
			if (size() > MAX && Thread.currentThread().getPriority() == Thread.NORM_PRIORITY)
			{
				log.debug("Notifier thread priority going from normal to max, size = "+size());
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			}
		}
		log.debug("Listener Notifier stopped");
		isStopped = true;
	}
	private void notifyListeners(SampleEvent res, List listeners)
	{
		Iterator iter;
		iter = listeners.iterator();
		while (iter.hasNext())
		{
			((SampleListener) iter.next()).sampleOccurred(res);
		}
	}
	public boolean isStopped()
	{
		return isStopped;
	}
	public synchronized void addLast(SampleEvent item, List listeners)
	{
		if(size() > ABS_MAX)
		{
			notifyListeners(item,listeners);		
		}
		else
		{
			super.addLast(item);
			super.addLast(listeners);
			sleepTime = 0;
		}
	}
	public synchronized Object removeFirst()
	{
		try
		{
			return super.removeFirst();
		}
		catch (RuntimeException e)
		{
			sleepTime = 2000;
			log.debug("Setting notifier thread priority to normal");
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			return null;
		}
	}
	public void stop()
	{
		running = false;
	}
	public void start()
	{
		Thread noteThread = new Thread(this);
		noteThread.start();
		isStopped = false;
	}
}
