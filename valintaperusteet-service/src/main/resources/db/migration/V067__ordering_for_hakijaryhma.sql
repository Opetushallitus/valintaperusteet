-- add necessary columns
ALTER TABLE hakijaryhma ADD COLUMN master_hakijaryhma_id int8;
ALTER TABLE hakijaryhma ADD CONSTRAINT fk_master_valinnan_vaihe_id FOREIGN KEY (master_hakijaryhma_id) REFERENCES hakijaryhma;
ALTER TABLE hakijaryhma ADD COLUMN edellinen_hakijaryhma_id int8;
ALTER TABLE hakijaryhma ADD CONSTRAINT fk_edellinen_hakijaryhma_id FOREIGN KEY (edellinen_hakijaryhma_id) REFERENCES hakijaryhma;
CREATE INDEX hakijaryhma_edellinen_hakijaryhma_id ON hakijaryhma(edellinen_hakijaryhma_id);

-- update hakijaryhma entries of all valintaryhma entries with more than one hakijaryhma with link info based on hakijaryhma ids in insertion order
UPDATE hakijaryhma hr
SET edellinen_hakijaryhma_id = prev_hr.prev_id
FROM (
  SELECT *
  FROM (
    SELECT hr.id, lag(hr.id) OVER (PARTITION by hr.valintaryhma_id ORDER BY hr.id) as prev_id
    FROM
      hakijaryhma hr
    WHERE
      hr.valintaryhma_id IS NOT NULL) as prev_hr_all
  WHERE prev_hr_all.prev_id IS NOT NULL) as prev_hr
WHERE hr.id = prev_hr.id;