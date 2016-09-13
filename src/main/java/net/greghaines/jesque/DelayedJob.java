package net.greghaines.jesque;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jonathanshin on 9/12/16.
 */
public class DelayedJob implements Serializable {

    private static final long serialVersionUID = -3470256227126193858L;

    private String queue;
    private String className;
    private Object[] args;

    /**
     * No-arg constructor.
     */
    public DelayedJob() {
        // Do nothing
    }

    /**
     * Cloning constructor. Makes a clone of the arguments, if they exist.
     *
     * @param origJob
     *            the Job to start from
     * @throws IllegalArgumentException
     *             if the origJob is null
     */
    public DelayedJob(final DelayedJob origJob) {
        if (origJob == null) {
            throw new IllegalArgumentException("origJob must not be null");
        }
        this.queue = origJob.queue;
        this.className = origJob.className;
        this.args = (origJob.args == null) ? null : origJob.args.clone();

    }

    /**
     * A convenience constructor. Delegates to Job(String, Object...) by calling
     * args.toArray().
     *
     * @param className
     *            the class name of the Job
     * @param args
     *            the arguments for the Job
     */
    public DelayedJob(final String queue, final String className, final List<?> args) {
        this(queue, className, args.toArray());
    }

    /**
     * Create a new Job with the given class name and arguments.
     *
     * @param className
     *            the class name of the Job
     * @param args
     *            the arguments for the Job
     */
    public DelayedJob(final String queue, final String className, final Object... args) {
        if ((queue == null || "".equals(queue)) && (className == null || "".equals(className))) {
            throw new IllegalArgumentException("className and queue must not be null or empty: " + className);
        }
        this.queue = queue;
        this.className = className;
        this.args = args;
    }

    /**
     *
     * @return name of target queue
     */
    public String getQueue() { return this.queue; }

    /**
     * Sets target queue name
     *
     * @param queue queue name
     */
    public void setQueue(final String queue) { this.queue = queue; }

    /**
     * @return the name of the Job's class
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Set the class name.
     *
     * @param className
     *            the new class name
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * @return the arguments for the job
     */
    public Object[] getArgs() {
        return this.args;
    }

    /**
     * Set the arguments.
     *
     * @param args
     *            the new arguments
     */
    public void setArgs(final Object[] args) {
        this.args = args;
    }


    @Override
    public String toString() {
        return "<Job queue=" + this.queue +" className=" + this.className + " args=" + Arrays.toString(this.args) + ">";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.args);
        result = prime * result + ((this.className == null) ? 0 : this.className.hashCode());
        result = prime * result + ((this.queue == null) ? 0 : this.queue.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DelayedJob other = (DelayedJob) obj;
        if (this.className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!this.queue.equals(other.queue) || !this.className.equals(other.className) || !Arrays.equals(this.args, other.args)) {
            return false;
        }
        return true;
    }
}
