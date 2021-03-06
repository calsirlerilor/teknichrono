package org.trd.app.teknichrono.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.ChronoManager;
import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Ping;
import org.trd.app.teknichrono.util.DurationLogger;

/**
 * 
 */
@Stateless
@Path("/pings")
public class PingEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;
  private Logger logger = Logger.getLogger(LapTimeEndpoint.class);

  @POST
  @Path("/create")
  @Consumes("application/json")
  public Response create(Ping entity, @QueryParam("chronoId") int chronoId, @QueryParam("beaconId") int beaconId) {
    try(DurationLogger dl = new DurationLogger(logger, "Ping for chronometer ID=" + chronoId + " and beacon ID=" + beaconId)) {
      Chronometer chrono = em.find(Chronometer.class, chronoId);
      if (chrono == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      entity.setChrono(chrono);
      Beacon beacon = em.find(Beacon.class, beaconId);
      if (beacon == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      entity.setBeacon(beacon);
      em.persist(entity);
      // TODO Check if relevant to create it each time...
      ChronoManager manager = new ChronoManager(em);
      manager.addPing(entity);
      return Response.created(UriBuilder.fromResource(PingEndpoint.class).path(String.valueOf(entity.getId())).build())
          .build();
    }
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    Ping entity = em.find(Ping.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") int id) {
    TypedQuery<Ping> findByIdQuery = em
        .createQuery("SELECT DISTINCT p FROM Ping p WHERE p.id = :entityId ORDER BY p.id", Ping.class);
    findByIdQuery.setParameter("entityId", id);
    Ping entity;
    try {
      entity = findByIdQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces("application/json")
  public List<Ping> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Ping> findAllQuery = em.createQuery("SELECT DISTINCT p FROM Ping p ORDER BY p.id", Ping.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Ping> results = findAllQuery.getResultList();
    return results;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Ping entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.getId()) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Ping.class, id) == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      entity = em.merge(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
