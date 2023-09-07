from pathlib import Path
import pandas as pd

class JSONHandler:
    def __init__(self, path):
        self.dataframe = pd.read_json(path)
    
    def get_konvolut_of_issue(self, ms_id):
        df = self.dataframe
        return df[df['MS-ID'] == ms_id]['Konvolut'].iloc[0]






