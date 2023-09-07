from __future__ import print_function, unicode_literals
import backend_db
#Import
import gitlab
import numpy as np
from PyInquirer import prompt

#Globale Variablen
gl = gitlab.Gitlab('https://gitlab.ub.uni-bielefeld.de/', private_token= '')
gl.auth()
project_id = 3786
ms_project = gl.projects.get(project_id)
ms_issues = ms_project.issues.list()
# this is the ID form Christoph
cg = 144
df = backend_db.read_data("issues.json")


def get_editable_issue(issue_id):
	issue = ms_issues[issue_id-1]
	editable_project = gl.projects.get(issue.project_id)
	editable_issue = editable_project.issues.get(issue.iid)
	return editable_issue

def show_issue(issue_id):
	issue = get_editable_issue(issue_id)
	return[issue.title, issue.id, issue.iid, issue.notes]


def user_ms_id():
	ms_id = prompt([{'type': 'input','name': 'ms_id','message': 'Which MS do you want to scan?'}])
	return ms_id

def create_issue(ms_id):

	ms = df[df["MS-ID"] == ms_id]
	konvolut = ms.iloc[0]["Konvolut"]
	print("Issue-ID: ", ms.iloc[0]["Issue-ID"])

	if ms.iloc[0]["Issue-ID"] is None or np.isnan(ms.iloc[0]["Issue-ID"]):
		current_date = date.today()
		issue_note = f"""
[Issuetitel: MS-ID: Titel]\n
{ms_id}]: Das ist der Ms-Titel\n

1. Scannen:\n 
* [x] MS-ID {ms_id}(Konvolut {konvolut}) gescannt und in Transferordner {current_date} abgelegt [LH]\n

2. Weiterverarbeitung I: Lese-pdf erzeugen\n
* [ ] Scan durchgesehen und umbenannt [CG]\n
* [ ] Nachscan erforderlich: [Link zum unten stehenden Beitrag mit der Auflistung der nachzuscannenden Seiten] [CG]\n
* [ ] Nachscans erfolgt [LH]\n
* [ ] pdf erzeugt: MS-ID.pdf inkl. Beilagen MS-IDa.pdf [CG]\n
* [ ] Erfassungstabelle aktualisiert [CG]\n

3. Weiterverarbeitung II: XML-Datei(an) anlegen\n
* [ ] XML-Datei(en) MS-ID.xml angelegt [CG]\n
* [ ] Beilagen vorhanden: [Link zu entsprechendem Issue mit der Beilagen-ID]\n
* [ ] Werk-XML nla_W_xxxx.xml angelegt bzw. dem Werk nla_W_xxxx.xml zugeordnet: [Link zum entsprechenden Werk-Issue] [CG]\n
* [ ] Diskussion über Zuordnung erforderlich: [Link zum unten stehenden Beitrag] [JS, CG]\n
* [ ] Diskussion abgeschlossen [JS, CG]\n
* [ ] Erfassungstabelle synchronisiert [CG]\n

4. Weiterverarbeitung III: OCR-Erstellung\n
* [ ] Roh-OCR erstellt & hochgeladen: [Link zum unten stehenden Beitrag mit der angehängten Text-Datei MS-ID_rohOCR.txt] [CG]\n
* [ ] bereinigte OCR-Datei erstellt & hochgeladen: [Link zum unten stehenden Beitrag mit der angehängten Word-Datei MS-ID_OCR-bereinigt.doc] [AH]\n

5. Weiterverarbeitung IV: Kodierung 1 (Standardkodierung für Portalanzeige & funktionale Kodierung)\n
* [ ] OCR in XML-Datei MS-ID.xml eingefügt und Kodierung (pb, lb) für Portaldarstellung durchgeführt [CG]\n
* [ ] funktionale Kodierung: Lit. ausgezeichnet, ZK-Verweise gesetzt\n

6. Weiterverarbeitung V: Kodierung 2 (vollständige Kodierung)\n
* [ ] vollständige Kodierung durchgeführt\n

7. Weiterverarbitung VI: Abschluss\n
* [ ] XML freigegeben zur Portalpräsentation\n
* [ ] Alles fertig!\n
		"""
		issue_dict = {'title': f'TEST {ms_id} ({konvolut})', 'description': issue_note}

		issue = ms_project.issues.create(issue_dict)
		issue.assignee_id =cg
		issue_id = issue.iid
		backend_db.df.loc[df['MS-ID'] == ms_id, ['Issue-ID']] = issue_id
		backend_db.df.loc[df['MS-ID'] == ms_id, ['created']] = date.today()
		backend_db.save_data(backend_db.conn, backend_db.df, "issue")
		issue.save()



	else:
		print("It already exists!")




