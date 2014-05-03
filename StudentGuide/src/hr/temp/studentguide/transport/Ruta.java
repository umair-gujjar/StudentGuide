package hr.temp.studentguide.transport;

public class Ruta {
	String ime;
	String url;
	
	Ruta(String ime, String url)
	{
		this.ime = ime;
		if(url.contains("http"))
		{
			this.url = url;
		}else
		{
			this.url = "http://www.zet.hr" + url;
		}
		
	}
	
	public String getIme() {
		return ime;
	}
	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ime == null) ? 0 : ime.hashCode());
		return result;
	}

	//Used to compare to 2 objects when adding to the list
	//...i neæe nikako proraditi
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ruta other = (Ruta) obj;
		if (ime == null) {
			if (other.ime != null)
				return false;
		} else if (!ime.substring(0, 3).equals(other.ime.substring(0, 3)))
			return false;
		return true;
	}
	
	
}
