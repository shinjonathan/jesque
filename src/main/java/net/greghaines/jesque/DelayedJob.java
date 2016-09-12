package net.greghaines.jesque;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jonathanshin on 9/12/16.
 */
public class DelayedJob implements Serializable {

    private static final long serialVersionUID = -3470256227126193858L;

    private String className;
    private Object[] args;
    private String queue;

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
        this.className = origJob.className;
        this.queue = origJob.queue;
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
    public DelayedJob(final String className, final String queue, final List<?> args) {
        this(className, queue, args.toArray());
    }

    /**
     * Create a new Job with the given class name and arguments.
     *
     * @param className
     *            the class name of the Job
     * @param args
     *            the arguments for the Job
     */
    public DelayedJob(final String className, final String queue, final Object... args) {
        if (className == null || "".equals(className)) {
            throw new IllegalArgumentException("className must not be null or empty: " + className);
        }
        this.className = className;
        this.args = args;
        this.queue = queue;
    }

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

    public String getQueue() { return this.queue; }

    public void setQueue(final String queue) { this.queue = queue; }
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


//    /**
//     * @return true if this Job has a valid class name and arguments
//     */
//    public boolean isValid() {
//        return (this.args != null && this.queue != null && !"".equals(queue) && this.className != null && !"".equals(this.className));
//    }

    @Override
    public String toString() {
        return "<Job className=" + this.className + " queue=" + this.queue + " args=" + Arrays.toString(this.args) + ">";
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
        } else if (!this.className.equals(other.className) || !this.queue.equals(other.queue) || !Arrays.equals(this.args, other.args)) {
            return false;
        }
        return true;
    }
}
