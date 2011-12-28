package models;

import java.io.Serializable;
import java.sql.*;
import javax.sql.*;
import java.math.*;
import java.util.*;
import java.util.Date;

public class ProductoId implements Serializable{

	public Long id;

	public Long sucursalId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sucursalId == null) ? 0 : sucursalId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProductoId)) {
			return false;
		}
		ProductoId other = (ProductoId) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (sucursalId == null) {
			if (other.sucursalId != null) {
				return false;
			}
		} else if (!sucursalId.equals(other.sucursalId)) {
			return false;
		}
		
		return true;
	}

}
