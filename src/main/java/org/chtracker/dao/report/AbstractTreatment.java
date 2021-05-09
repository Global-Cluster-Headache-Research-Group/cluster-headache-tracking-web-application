package org.chtracker.dao.report;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@MappedSuperclass
public abstract class AbstractTreatment {

    @NotNull
    private LocalDateTime started;
    private LocalDateTime stopped;

    @Positive
    private int doze;

    @Size(max = 1000)
    private String comments;

    AbstractTreatment() {
    }

    /**
     * Abstract method that returns id of an entity.
     * 
     * @return id
     */
    abstract int getId();

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getStopped() {
        return stopped;
    }

    public void setStopped(LocalDateTime stopped) {
        this.stopped = stopped;
    }

    public int getDoze() {
        return doze;
    }

    public void setDoze(int doze) {
        this.doze = doze;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public int hashCode() {
        return started.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractTreatment other = (AbstractTreatment) obj;
        return started.equals(other.started);
    }

}
