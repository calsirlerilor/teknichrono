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

import org.trd.app.teknichrono.model.Location;
import org.trd.app.teknichrono.model.Session;

/**
 * 
 */
@Stateless
@Path("/locations")
public class LocationEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Location entity) {
    em.persist(entity);
    return Response
        .created(UriBuilder.fromResource(LocationEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    Location entity = em.find(Location.class, id);
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
    TypedQuery<Location> findByIdQuery = em.createQuery(
        "SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions WHERE e.id = :entityId ORDER BY e.id",
        Location.class);
    findByIdQuery.setParameter("entityId", id);
    Location entity;
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
  @Path("/name")
  @Produces("application/json")
  public Location findLocationByName(@QueryParam("name") String name) {
    TypedQuery<Location> findByNameQuery = em.createQuery(
        "SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions WHERE e.name = :name ORDER BY e.id",
        Location.class);
    findByNameQuery.setParameter("name", name);
    Location entity;
    try {
      entity = findByNameQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    return entity;
  }

  @GET
  @Produces("application/json")
  public List<Location> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Location> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions ORDER BY e.id", Location.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Location> results = findAllQuery.getResultList();
    return results;
  }

  @POST
  @Path("{locationId:[0-9][0-9]*}/addSession")
  @Produces("application/json")
  public Response addSession(@PathParam("locationId") int locationId, @QueryParam("sessionId") Integer sessionId) {
    Location location = em.find(Location.class, locationId);
    if (location == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    session.setLocation(location);
    location.getSessions().add(session);
    em.persist(location);
    em.persist(session);

    return Response.ok(location).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Location entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.getId()) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Location.class, id) == null) {
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
