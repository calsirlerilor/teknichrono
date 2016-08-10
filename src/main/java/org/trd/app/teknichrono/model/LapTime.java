package org.trd.app.teknichrono.model;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class LapTime implements java.io.Serializable {

  /* =========================== Entity stuff =========================== */
  /**
   * 
   */
  private static final long serialVersionUID = -2438563507266191424L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private int id;

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @ManyToOne
  private Pilot pilot;

  // Used to order the laps for the pilot relationship
  @Column
  private Timestamp startDate;

  @ManyToOne(fetch = FetchType.LAZY)
  private Event event;

  @OneToMany
  @OrderColumn(name = "dateTime")
  private List<Ping> intermediates = new ArrayList<Ping>();

  /* ===================== Getters and setters ======================== */

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Pilot getPilot() {
    return pilot;
  }

  public void setPilot(Pilot pilot) {
    this.pilot = pilot;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp captureDate) {
    this.startDate = captureDate;
  }

  public Event getEvent() {
    return this.event;
  }

  public void setEvent(final Event event) {
    this.event = event;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "id: " + id;
    return result;
  }

  public List<Ping> getIntermediates() {
    return this.intermediates;
  }

  public void setIntermediates(final List<Ping> intermediates) {
    this.intermediates = intermediates;
  }

  public void addIntermediates(Ping p) {
    addIntermediates(this.intermediates.size(), p);
  }

  public void addIntermediates(int index, Ping p) {
    if (startDate == null || startDate.getTime() > p.getDateTime().getTime()) {
      startDate = p.getDateTime();
    }
    this.intermediates.add(index, p);
  }

}
